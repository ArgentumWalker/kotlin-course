package ru.spbau.mit.evaluation_tree

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor
import ru.spbau.mit.parser.langBaseVisitor
import ru.spbau.mit.parser.langLexer
import ru.spbau.mit.parser.langParser
import ru.spbau.mit.parser.langVisitor
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream

class EvaluationTree(val rootNode: Node) {

    fun evaluate(): Value {
        return rootNode.exec(Scope.defaultScope())
    }
}

object EvaluationTreeBuilderVisitor: langVisitor<Node>, AbstractParseTreeVisitor<Node>() {

    fun buildTree(code: String): EvaluationTree {
        return buildTree(CharStreams.fromString(code));
    }

    fun buildTree(file: File): EvaluationTree {
        return buildTree(CharStreams.fromFileName(file.absolutePath))
    }

    fun buildTree(input: CharStream): EvaluationTree {
        return EvaluationTree(EvaluationTreeBuilderVisitor
                .visitFile(langParser(BufferedTokenStream(langLexer(input))).file()))
    }


    override fun visitFile(ctx: langParser.FileContext): Block {
        return visitBlock(ctx.block())
    }

    override fun visitBlock(ctx: langParser.BlockContext): Block {
        val sts: MutableList<Statement> = mutableListOf()
        ctx.statement().mapTo(sts) { visitStatement(it) }
        return Block(sts, ctx.start.line.toLong())
    }

    override fun visitBlockWithBraces(ctx: langParser.BlockWithBracesContext): Block {
        return visitBlock(ctx.block())
    }

    override fun visitStatement(ctx: langParser.StatementContext): Statement {
        return (ctx.getChild(0).accept(this)) as Statement
    }

    override fun visitReturn_st(ctx: langParser.Return_stContext): Return {
        return Return(visitExpression(ctx.expression()), ctx.start.line.toLong())
    }

    override fun visitFunction(ctx: langParser.FunctionContext): Function {
        return Function(ctx.Identifier().text, visitParameters(ctx.parameters()),
                visitBlockWithBraces(ctx.blockWithBraces()), ctx.start.line.toLong())
    }

    override fun visitParameters(ctx: langParser.ParametersContext?): Parameters {
        val params: MutableList<String> = mutableListOf()
        if (ctx != null) {
            var ptr: langParser.ParametersContext? = ctx
            while (ptr != null) {
                params.add(ptr.Identifier().text)
                ptr = ptr.parameters()
            }
        }
        return Parameters(params, ctx?.start?.line?.toLong() ?: -1)
    }

    override fun visitFunctionCall(ctx: langParser.FunctionCallContext): FunctionCall {
        return FunctionCall(ctx.Identifier().text, visitArguments(ctx.arguments()), ctx.start.line.toLong())
    }

    override fun visitArguments(ctx: langParser.ArgumentsContext?): Arguments {
        val args: MutableList<Expression> = mutableListOf()
        if (ctx != null) {
            var ptr: langParser.ArgumentsContext? = ctx
            while (ptr != null) {
                args.add(visitExpression(ptr.expression()))
                ptr = ptr.arguments()
            }
        }
        return Arguments(args, ctx?.start?.line?.toLong() ?: -1)
    }

    override fun visitVariable(ctx: langParser.VariableContext): Variable {
        return Variable(ctx.Identifier().text,
                if (ctx.expression() != null) visitExpression(ctx.expression()) else null, ctx.start.line.toLong())
   }

    override fun visitValueAssignment(ctx: langParser.ValueAssignmentContext): VariableValueAssign {
        return VariableValueAssign(ctx.Identifier().text, visitExpression(ctx.expression()), ctx.start.line.toLong())
    }

    override fun visitAssignment(ctx: langParser.AssignmentContext): VariableAssign {
        return VariableAssign(ctx.Identifier().text, visitExpression(ctx.expression()), ctx.start.line.toLong())
    }

    override fun visitWhile_st(ctx: langParser.While_stContext): While {
        return While(visitExpression(ctx.expression()), visitBlockWithBraces(ctx.blockWithBraces()), ctx.start.line.toLong())
    }

    override fun visitIf_st(ctx: langParser.If_stContext): If {
        return If(visitExpression(ctx.expression()), visitBlockWithBraces(ctx.blockWithBraces(0)),
                if (ctx.blockWithBraces().size > 1) visitBlockWithBraces(ctx.blockWithBraces(1)) else null, ctx.start.line.toLong())
    }

    override fun visitExpression(ctx: langParser.ExpressionContext): Expression {
        if (ctx.expression() != null) {
            return visitExpression(ctx.expression())
        }
        return (ctx.getChild(0).accept(this)) as Expression
    }

    override fun visitAtomicExpression(ctx: langParser.AtomicExpressionContext): Expression {
        if (ctx.Literal() != null) {
            return Value(ctx.Literal().text.toLong(), ctx.start.line.toLong())
        }
        if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall())
        }
        if (ctx.Identifier() != null) {
            return VariableCall(ctx.Identifier().text, ctx.start.line.toLong())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitBinaryExpression(ctx: langParser.BinaryExpressionContext): Expression {
        return (ctx.getChild(0).accept(this)) as Expression
    }

    override fun visitBoolExpression(ctx: langParser.BoolExpressionContext): Expression {
        val left: Expression = visitBoolExpressionHelper(ctx.boolExpressionHelper(0))
        val right: Expression =
                if (ctx.boolExpression() != null) {
                    visitBoolExpression(ctx.boolExpression())
                } else {
                    visitBoolExpressionHelper(ctx.boolExpressionHelper(1))
                }
        when (ctx.BoolOp().symbol.text) {
            "||" -> return BinaryExpression(left, OperationType.OR, right, ctx.start.line.toLong())
            "&&" -> return BinaryExpression(left, OperationType.AND, right, ctx.start.line.toLong())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitBoolExpressionHelper(ctx: langParser.BoolExpressionHelperContext): Expression {
        return (ctx.getChild(0).accept(this)) as Expression
    }

    override fun visitEqExpression(ctx: langParser.EqExpressionContext): Expression {
        val left: Expression = visitEqExpressionHelper(ctx.eqExpressionHelper(0))
        val right: Expression =
                if (ctx.eqExpression() != null) {
                    visitEqExpression(ctx.eqExpression())
                } else {
                    visitEqExpressionHelper(ctx.eqExpressionHelper(1))
                }
        when (ctx.EqOp().symbol.text) {
            "<" -> return BinaryExpression(left, OperationType.LESS, right, ctx.start.line.toLong())
            "<=" -> return BinaryExpression(left, OperationType.LEQ, right, ctx.start.line.toLong())
            ">" -> return BinaryExpression(left, OperationType.GRET, right, ctx.start.line.toLong())
            ">=" -> return BinaryExpression(left, OperationType.GEQ, right, ctx.start.line.toLong())
            "==" -> return BinaryExpression(left, OperationType.EQ,  right, ctx.start.line.toLong())
            "!=" -> return BinaryExpression(left, OperationType.NEQ, right, ctx.start.line.toLong())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitEqExpressionHelper(ctx: langParser.EqExpressionHelperContext): Expression {
        return (ctx.getChild(0).accept(this)) as Expression
    }

    override fun visitMdExpression(ctx: langParser.MdExpressionContext): Expression {
        val left: Expression = visitMdExpressionHelper(ctx.mdExpressionHelper(0))
        val right: Expression =
                if (ctx.mdExpression() != null) {
                    visitMdExpression(ctx.mdExpression())
                } else {
                    visitMdExpressionHelper(ctx.mdExpressionHelper(1))
                }
        when (ctx.MdOp().symbol.text) {
            "*" -> return BinaryExpression(left, OperationType.MULT, right, ctx.start.line.toLong())
            "/" -> return BinaryExpression(left, OperationType.DIVIDE, right, ctx.start.line.toLong())
            "%" -> return BinaryExpression(left, OperationType.MOD, right, ctx.start.line.toLong())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitMdExpressionHelper(ctx: langParser.MdExpressionHelperContext): Expression {
        return (ctx.getChild(0).accept(this)) as Expression
    }

    override fun visitPmExpression(ctx: langParser.PmExpressionContext): Expression {
        val left: Expression = visitPmExpressionHelper(ctx.pmExpressionHelper(0))
        val right: Expression =
                if (ctx.pmExpression() != null) {
                    visitPmExpression(ctx.pmExpression())
                } else {
                    visitPmExpressionHelper(ctx.pmExpressionHelper(1))
                }
        when (ctx.PmOp().symbol.text) {
            "+" -> return BinaryExpression(left, OperationType.PLUS, right, ctx.start.line.toLong())
            "-" -> return BinaryExpression(left, OperationType.MINUS, right, ctx.start.line.toLong())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitPmExpressionHelper(ctx: langParser.PmExpressionHelperContext): Expression {
        if (ctx.expression() != null) {
            return visitExpression(ctx.expression())
        }
        return (ctx.getChild(0).accept(this)) as Expression
    }

}

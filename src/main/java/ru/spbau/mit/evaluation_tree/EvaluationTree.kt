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

class EvaluationTree {
    val rootNode: Node

    constructor(code: String) {
        rootNode = EvaluationTreeBuilderVisitor
                .visitFile(langParser(BufferedTokenStream(langLexer(CharStreams.fromString(code)))).file())
    }

    constructor(file: File) {
        rootNode = EvaluationTreeBuilderVisitor
                .visitFile(langParser(BufferedTokenStream(langLexer(CharStreams.fromFileName(file.absolutePath)))).file())
    }

    fun evaluate(): Value {
        return rootNode.exec(Scope.defaultScope())
    }
}

object EvaluationTreeBuilderVisitor: langVisitor<Node>, AbstractParseTreeVisitor<Node>() {
    override fun visitFile(ctx: langParser.FileContext?): Block {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return visitBlock(ctx.block())
    }

    override fun visitBlock(ctx: langParser.BlockContext?): Block {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        val sts: MutableList<Statement> = mutableListOf()
        ctx.statement().mapTo(sts) { visitStatement(it) }
        return Block(sts, ctx.start.line.toLong())
    }

    override fun visitBlockWithBraces(ctx: langParser.BlockWithBracesContext?): Block {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return visitBlock(ctx.block())
    }

    override fun visitStatement(ctx: langParser.StatementContext?): Statement {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        if (ctx.if_st() != null) {
            return visitIf_st(ctx.if_st())
        }
        if (ctx.while_st() != null) {
            return visitWhile_st(ctx.while_st())
        }
        if (ctx.assignment() != null) {
            return visitAssignment(ctx.assignment())
        }
        if (ctx.valueAssignment() != null) {
            return visitValueAssignment(ctx.valueAssignment())
        }
        if (ctx.expression() != null) {
            return visitExpression(ctx.expression())
        }
        if (ctx.return_st() != null) {
            return visitReturn_st(ctx.return_st())
        }
        if (ctx.function() != null) {
            return visitFunction(ctx.function())
        }
        if (ctx.variable() != null) {
            return visitVariable(ctx.variable())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitReturn_st(ctx: langParser.Return_stContext?): Return {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return Return(visitExpression(ctx.expression()), ctx.start.line.toLong())
    }

    override fun visitFunction(ctx: langParser.FunctionContext?): Function {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return Function(ctx.Identifier().text, visitParameters(ctx.parameters()),
                visitBlockWithBraces(ctx.blockWithBraces()), ctx.start.line.toLong())
    }

    override fun visitParameters(ctx: langParser.ParametersContext?): Parameters {
        if (ctx == null) {
            return Parameters(listOf(), -1)
        }
        val params: MutableList<String> = mutableListOf()
        var ptr: langParser.ParametersContext? = ctx
        while (ptr != null) {
            params.add(ptr.Identifier().text)
            ptr = ptr.parameters()
        }
        return Parameters(params, ctx.start.line.toLong())
    }

    override fun visitFunctionCall(ctx: langParser.FunctionCallContext?): FunctionCall {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return FunctionCall(ctx.Identifier().text, visitArguments(ctx.arguments()), ctx.start.line.toLong())
    }

    override fun visitArguments(ctx: langParser.ArgumentsContext?): Arguments {
        if (ctx == null) {
            return Arguments(listOf(), -1)
        }
        val args: MutableList<Expression> = mutableListOf()
        var ptr: langParser.ArgumentsContext? = ctx
        while (ptr != null) {
            args.add(visitExpression(ptr.expression()))
            ptr = ptr.arguments()
        }
        return Arguments(args, ctx.start.line.toLong())
    }

    override fun visitVariable(ctx: langParser.VariableContext?): Variable {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return Variable(ctx.Identifier().text,
                if (ctx.expression() != null) visitExpression(ctx.expression()) else null, ctx.start.line.toLong())
   }

    override fun visitValueAssignment(ctx: langParser.ValueAssignmentContext?): VariableValueAssign {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return VariableValueAssign(ctx.Identifier().text, visitExpression(ctx.expression()), ctx.start.line.toLong())
    }

    override fun visitAssignment(ctx: langParser.AssignmentContext?): VariableAssign {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return VariableAssign(ctx.Identifier().text, visitExpression(ctx.expression()), ctx.start.line.toLong())
    }

    override fun visitWhile_st(ctx: langParser.While_stContext?): While {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return While(visitExpression(ctx.expression()), visitBlockWithBraces(ctx.blockWithBraces()), ctx.start.line.toLong())
    }

    override fun visitIf_st(ctx: langParser.If_stContext?): If {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        return If(visitExpression(ctx.expression()), visitBlockWithBraces(ctx.blockWithBraces(0)),
                if (ctx.blockWithBraces().size > 1) visitBlockWithBraces(ctx.blockWithBraces(1)) else null, ctx.start.line.toLong())
    }

    override fun visitExpression(ctx: langParser.ExpressionContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        if (ctx.atomicExpression() != null) {
            return visitAtomicExpression(ctx.atomicExpression())
        }
        if (ctx.binaryExpression() != null) {
            return visitBinaryExpression(ctx.binaryExpression())
        }
        if (ctx.expression() != null) {
            return visitExpression(ctx.expression())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitAtomicExpression(ctx: langParser.AtomicExpressionContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        if (ctx.Identifier() != null) {
            return VariableCall(ctx.Identifier().text, ctx.start.line.toLong())
        }
        if (ctx.Literal() != null) {
            return Value(ctx.Literal().text.toLong(), ctx.start.line.toLong())
        }
        if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitBinaryExpression(ctx: langParser.BinaryExpressionContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        if (ctx.boolExpression() != null) {
            return visitBoolExpression(ctx.boolExpression())
        }
        if (ctx.eqExpression() != null) {
            return visitEqExpression(ctx.eqExpression())
        }
        if (ctx.mdExpression() != null) {
            return visitMdExpression(ctx.mdExpression())
        }
        if (ctx.pmExpression() != null) {
            return visitPmExpression(ctx.pmExpression())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitBoolExpression(ctx: langParser.BoolExpressionContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
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

    override fun visitBoolExpressionHelper(ctx: langParser.BoolExpressionHelperContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        if (ctx.eqExpression() != null) {
            return visitEqExpression(ctx.eqExpression())
        }
        if (ctx.eqExpressionHelper() != null) {
            return visitEqExpressionHelper(ctx.eqExpressionHelper())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitEqExpression(ctx: langParser.EqExpressionContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
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

    override fun visitEqExpressionHelper(ctx: langParser.EqExpressionHelperContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        if (ctx.mdExpression() != null) {
            return visitMdExpression(ctx.mdExpression())
        }
        if (ctx.mdExpressionHelper() != null) {
            return visitMdExpressionHelper(ctx.mdExpressionHelper())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitMdExpression(ctx: langParser.MdExpressionContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
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

    override fun visitMdExpressionHelper(ctx: langParser.MdExpressionHelperContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        if (ctx.pmExpression() != null) {
            return visitPmExpression(ctx.pmExpression())
        }
        if (ctx.pmExpressionHelper() != null) {
            return visitPmExpressionHelper(ctx.pmExpressionHelper())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

    override fun visitPmExpression(ctx: langParser.PmExpressionContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
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

    override fun visitPmExpressionHelper(ctx: langParser.PmExpressionHelperContext?): Expression {
        if (ctx == null) {
            throw ParsingException(-1)
        }
        if (ctx.atomicExpression() != null) {
            return visitAtomicExpression(ctx.atomicExpression())
        }
        if (ctx.expression() != null) {
            return visitExpression(ctx.expression())
        }
        throw ParsingException(ctx.start.line.toLong())
    }

}

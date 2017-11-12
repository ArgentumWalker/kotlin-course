grammar lang;

//LEXER RULES
EqOp: '<' | '<=' | '==' | '>=' | '>' | '!=';
BoolOp: '||' | '&&';
MdOp: '*' | '/' | '%';
PmOp: '+' | '-';

//Complex
Identifier: [a-zA-Z_][0-9a-zA-Z_]*;
Literal: '-'?[1-9][0-9]* | '0';

//Skip
LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;

Whitespace : (' ' | '\t' | '\r'| '\n') -> skip;

//PARSER RULES
//Base
file: block;

block: statement*;

blockWithBraces: '{' block '}';

statement
    : function
    | variable
    | expression
    | while_st
    | if_st
    | assignment
    | valueAssignment
    | return_st
    ;

return_st: 'return' expression;

//Function

function: 'fun' Identifier '(' (parameters)? ')' blockWithBraces;

parameters: Identifier (',' parameters)?;

functionCall: Identifier '(' (arguments)? ')';

arguments: expression (',' arguments)?;

//Variables
variable: 'var' Identifier ('=' expression)?;

valueAssignment: Identifier '=' expression;

assignment: Identifier ':=' expression;

//Complex statements
while_st: 'while' '(' expression ')' blockWithBraces;

if_st: 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?;

//Expression
expression: binaryExpression | '(' expression ')' | atomicExpression;

atomicExpression: functionCall | Identifier | Literal;

binaryExpression: eqExpression | boolExpression | mdExpression | pmExpression;

boolExpression: boolExpressionHelper BoolOp (boolExpressionHelper | boolExpression);
boolExpressionHelper: eqExpressionHelper | eqExpression;

eqExpression: eqExpressionHelper EqOp (eqExpressionHelper | eqExpression);
eqExpressionHelper: mdExpressionHelper | mdExpression;

mdExpression: mdExpressionHelper MdOp (mdExpressionHelper | mdExpression);
mdExpressionHelper: pmExpressionHelper | pmExpression;

pmExpression: pmExpressionHelper PmOp (pmExpressionHelper | pmExpression);
pmExpressionHelper: atomicExpression | '(' expression ')';

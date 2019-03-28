package com.ing;

//project to parse oracle-plsql-files for Vortex
//using antlr
//goal: get meta-data information out of sql-files
//like  - mapping tables to procedures (CRUD-matrix (on field level?))
//      - show data-lineage from sp-files to datamart
//      - show data-lineage from static-data to impact fields
//      - unused procedures / tables
//      - etc

//antlr - grammar & lexer from https://github.com/antlr/grammars-v4/tree/master/plsql
// import ANTLR's runtime libraries

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.FileWriter;

//problem: lexer is for 11g not 12c
//errors with $$plsql_unit
//$$ is: Inquiry Directives
//        An inquiry directive provides information about the compilation environment.
//        Syntax
//        $$name
//        For information about name, which is an unquoted PL/SQL identifier, see "Identifiers".
//        An inquiry directive typically appears in the boolean_static_expression of a selection directive, but it can appear anywhere that a variable or literal of its type can appear. Moreover, it can appear where regular PL/SQL allows only a literal (not a variable)—for example, to specify the size of a VARCHAR2 variable.


public class Main {

    public static String outpath = "D:\\projects\\plsqlcode\\out\\";

    public static void parse(Path filename) {

        try {
            /*
             * get the input file as an InputStream
//            CharStream s = CharStreams.fromPath(Paths.get(filename)); */
            System.out.println(filename.toString());

            CharStream s = CharStreams.fromPath(filename);
            CaseChangingCharStream upper = new CaseChangingCharStream(s, true);
            /*
             * make Lexer
             */
            PlSqlLexer lexer = new PlSqlLexer(upper);
            /*
             * get a TokenStream on the Lexer
             */
            TokenStream tokenStream = new CommonTokenStream(lexer);
            /*
             * make a Parser on the token stream
             */
            PlSqlParser parser = new PlSqlParser(tokenStream);

            ParseTree tree = parser.sql_script(); // begin parsing at init rule
//            System.out.println(tree.toStringTree(parser)); // print LISP-style tree

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outpath + filename.getName(filename.getNameCount() - 1) + ".out"))) {
                String content = tree.toStringTree(parser);
                bw.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
             * get the top node of the AST. This corresponds to the topmost rule of equation.q4, "equation"
            PlSqlParser.Tsql_fileContext tree = parser.sql_script();

            ParseTreeWalker walker = new ParseTreeWalker();
            List<String> ruleNames = Arrays.asList(parser.getRuleNames());
            TSqlXmlWriter writer = new TSqlXmlWriter(xsw,ruleNames);
            try {
                xsw.writeStartDocument();
                xsw.writeStartElement("sql");
                walker.walk(writer, tree);
                xsw.writeEndElement();
                xsw.writeEndDocument();
                xsw.flush();
                xsw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
//            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        try {

            if (true) {
                //  to parse one file
//            String filename_to_parse = "D:\\projects\\plsqlcode\\dsa\\add_till_reduction_date.sql";   //INQUIRY_DIRECTIVE
//            String filename_to_parse = "D:\\projects\\plsqlcode\\dsa\\db_sizes.sql";     //EDITIONABLE
                String filename_to_parse = "D:\\projects\\plsqlcode\\dsa\\determine_cover_start_date.sql";     //PERIOD-issue?
//                String filename_to_parse = "D:\\projects\\plsqlcode\\dsa\\fac_limit_amount_default.sql";   //superfluous enter SQL%rowcount [ERROR IN CODE]
//                String filename_to_parse = "D:\\projects\\plsqlcode\\dsa\\vortex_integrity_check.sql";   //$IF, $THEN, $ELSE, $END


                Path pathfile = Paths.get(filename_to_parse);
                parse(pathfile);
            } else {
                //to parse direcory
//            String directory_to_parse = "D:\\projects\\plsqlcode\\examples";        //test-examples
                String directory_to_parse = "D:\\projects\\plsqlcode\\examples-sql-script";        //test-examples

//            String directory_to_parse = "D:\\projects\\plsqlcode\\dsa";
//          String directory_to_parse = "D:\\projects\\plsqlcode\\sdp";

                //make sure we also save potential errors from the parsing
                PrintStream fileOut = new PrintStream("D:\\projects\\plsqlcode\\out\\_console-out.txt");
                PrintStream fileErr = new PrintStream("D:\\projects\\plsqlcode\\out\\_console-err.txt");
                System.setOut(fileOut);
                System.setErr(fileOut);

                Files.newDirectoryStream(Paths.get(directory_to_parse), path -> path.toFile().isFile() && path.toString().contains("sql"))
                        .forEach(s -> parse(s));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

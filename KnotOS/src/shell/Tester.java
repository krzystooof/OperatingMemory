package shell;

import java.util.ArrayList;

public class Tester implements Shell {
    private int testNum = 0;

    @Override
    public ArrayList<String> getShellCommands() {
        ArrayList<String> ret =  new ArrayList<String>();
        ret.add("tests");
        return ret;
    }

    @Override
    public void pass(ArrayList<String> params) {
        test();
    }

    @Override
    public void getHelp() {
        System.out.println("Help regarding system tests:\n" +
                "tests");
    }

    @Override
    public String getName() {
        return "Testing module";
    }

    private void test() {

        if (Interface.askUserYN("This test will result in system exit. Do you want to continue?")) {


            //HEADERS
            printTestHeader("tests", "Testing header printing", "Printed header");

            printTestHeader("tests", "Testing headers with really long descriptions to test breaking strings into lines. " +
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Suspendisse ullamcorper neque sodales ultricies scelerisque. " +
                    "Sed facilisis euismod tellus, eu aliquet nulla posuere et. " +
                    "Praesent tempor nisi et facilisis dapibus. " +
                    "Nam cursus, augue ut semper ornare, risus risus feugiat velit, ac dictum diam libero a urna. " +
                    "Vivamus at lacus nec quam tincidunt laoreet. Vestibulum tincidunt imperdiet urna et viverra. " +
                    "Morbi nec turpis ornare, molestie nunc vitae, mollis ipsum. Donec a nunc eu mauris accumsan iaculis. " +
                    "Etiam mollis quam eget justo maximus bibendum. Vestibulum a mi nec tortor interdum mollis at sit amet enim. " +
                    "Ut bibendum ultricies nulla in porttitor. Duis vel risus leo. " +
                    "Aenean ornare, urna eleifend iaculis auctor, erat mi venenatis leo, quis iaculis nibh ante in purus. " +
                    "Aliquam hendrerit semper ultricies.", "Printed header");

            //INTERFACE
            printTestHeader("Interface", "Testing posting", "Hello world!");
            Interface.post("Hello world!");

            printTestHeader("Interface", "Testing Y/N questions", "question");
            while (!Interface.askUserYN("Do you see this question? First answer \"n\"")) ;

            printTestHeader("Interface", "Testing questions", "Question");
            Interface.askUser("If this appeared on the screen then everything is working. Press enter");

            printTestHeader("Interface", "Testing help", "All help");
            Interface.getHelp();

            //FILESYSTEM
            printTestHeader("Filesystem", "Testing help", "Filesystem help");
            Interface.pass("cd help");

            printTestHeader("Filesystem", "Testing for filesystem movement", "No parent; No dir; No dir");
            Interface.pass("cd ..");
            Interface.pass("cd winrar");
            Interface.pass("cd *");

            printTestHeader("Filesystem", "Testing for folder creation", "created; created; created");
            Interface.pass("mkdir testfolder");
            Interface.pass("cd testfolder");
            Interface.pass("mkdir testfolder");
            Interface.pass("mkdir testfolder2");
            Interface.pass("mkdir testfolder3");

            printTestHeader("Filesystem", "Testing for folder creation", "fail");
            Interface.pass("mkdir \\");

            printTestHeader("Filesystem", "Testing for folder removal", "success; check drive C for testfolder, 2 dir within");
            Interface.pass("rmdir testfolder3");

            printTestHeader("Filesystem", "Testing for filesystem movement", "No parent");
            Interface.pass("cd ..");
            Interface.pass("cd ..");

            while (!Interface.askUserYN("Did you check C drive?")) ;

            printTestHeader("Filesystem cleanup", "Test folders are being removed now", "-");
            Interface.pass("cd testfolder");
            Interface.pass("rmdir testfolder");
            Interface.pass("rmdir testfolder2");
            Interface.pass("cd ..");
            Interface.pass("rmdir testfolder");

            //PROCESS TODO when assembly files available
            //printTestHeader("semaphores.Process", "start", "-");

            //USER TODO when debugged
            printTestHeader("User", "Test user adding", "5 users added; Total 6");
            Interface.pass("user -add test test");
            Interface.pass("user -add test1 test1");
            Interface.pass("user -add test2 test2");
            Interface.pass("user -add test3 test3");
            Interface.pass("user -add test4 test4");
            Interface.pass("user -list");

            printTestHeader("User", "Test user removal", "4 users deleted; Total 2");
            Interface.pass("user -delete test1 test1");
            Interface.pass("user -delete test2 test2");
            Interface.pass("user -delete test3 test3");
            Interface.pass("user -delete test4 test4");
            Interface.pass("user -list");

            printTestHeader("User", "Test user duplication", "2x added;2x failed");
            Interface.pass("user -add test1 test1");
            Interface.pass("user -add test2 test1");
            Interface.pass("user -add test1 test1");
            Interface.pass("user -add test test");

            printTestHeader("User", "Test user logging", "Logged as \"test\"");
            while (!Interface.askUserYN("Please make sure you know username and password of currently logged user. Please log in now as test:test. Input \"y\" to continue"))
                ;
            Interface.pass("logout");

            printTestHeader("User", "Test user password changing", "Password changed");
            Interface.pass("password change test test1");

            printTestHeader("User", "Test user removal", "2x removed;2x failed; Total 2");
            Interface.pass("user -delete test1 test1");
            Interface.pass("user -delete test2 test1");
            Interface.pass("user -delete test test");
            Interface.pass("user -delete test2 test1");
            Interface.pass("user -list");

            printTestHeader("User Cleanup", "Test user logging", "Logged as previous user; User test deleted; Total 1");
            while (!Interface.askUserYN("Please login now as previous user. Input \"y\" to continue")) ;
            Interface.pass("logout");
            Interface.pass("user -delete test test1");
            Interface.pass("user -list");

            printTestHeader("User", "Test user logging between sessions", "Logged as previous user; Total 1");
            while (!Interface.askUserYN("System will now close. Input \"y\" to continue"));
            Interface.pass("exit");
            Interface.pass("user -list");

        } else Interface.post("Tests aborted");
    }

    private void printTestHeader(String module, String description, String expected) {
        boolean newline;
        String newlineString = null;
        System.out.println("---------------------------------------------------");
        System.out.println("|                  TEST " + Integer.toString(testNum) + "                         |");
        testNum++;
        if (module.length() > 32) {
            System.out.print("| Tested module: " + module.substring(0,32));
            newline = true;
            newlineString = module.substring(32);
            for (int i = 0; i < (32 - module.substring(0,32).length()); i++) System.out.print(" ");
        } else {
            System.out.print("| Tested module: " + module);
            newline = false;
            for (int i = 0; i < (32 - module.length()); i++) System.out.print(" ");
        }
        System.out.println(" |");

        while (newline) {
            if (newlineString.length() > 46) {
                System.out.print("| " + newlineString.substring(0,46) + " |\n");
                newline = true;
                newlineString = newlineString.substring(46);
            } else {
                System.out.print("| " + newlineString);
                for (int i = 0; i != (47 - newlineString.length()); i++) System.out.print(" ");
                System.out.print(" |\n");
                newline = false;
            }
        }
        if (description.length() > 34) {
            System.out.print("| Description: " + description.substring(0,34));
            newline = true;
            newlineString = description.substring(34);
            for (int i = 0; i < (34 - description.substring(0,34).length()); i++) System.out.print(" ");
        } else {
            System.out.print("| Description: " + description);
            newline = false;
            for (int i = 0; i < (34 - description.length()); i++) System.out.print(" ");
        }
        System.out.println(" |");

        while (newline) {
            if (newlineString.length() > 46) {
                System.out.print("| " + newlineString.substring(0,46) + "  |\n");
                newline = true;
                newlineString = newlineString.substring(46);
            } else {
                System.out.print("| " + newlineString);
                for (int i = 0; i != (47 - newlineString.length()); i++) System.out.print(" ");
                System.out.print(" |\n");
                newline = false;
            }
        }

        if (expected.length() > 30) {
            System.out.print("| Expected result: " + expected.substring(0,30));
            newline = true;
            newlineString = expected.substring(30);
            for (int i = 0; i < (30 - expected.substring(0,30).length()); i++) System.out.print(" ");
        } else {
            System.out.print("| Expected result: " + expected);
            newline = false;
            for (int i = 0; i < (30 - expected.length()); i++) System.out.print(" ");
        }
        System.out.println(" |");

        while (newline) {
            if (newlineString.length() > 46) {
                System.out.print("| " + newlineString.substring(0,46) + " |\n");
                newline = true;
                newlineString = newlineString.substring(46);
            } else {
                System.out.print("| " + newlineString);
                for (int i = 0; i != (47 - newlineString.length()); i++) System.out.print(" ");
                System.out.print(" |\n");
                newline = false;
            }
        }
        System.out.println("---------------------------------------------------");
    }
}

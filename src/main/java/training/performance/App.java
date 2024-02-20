package training.performance;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        App appLoc = new App();
        appLoc.method1("abc");
        if (true) {
            String stringLoc = "osman" + args;
            System.out.println(stringLoc);
        }
        System.out.println("deneme");
        appLoc.method2("osman" + args,1970);
    }


    public void method1(String abc) {
         // if (logger.isDebugEnabled())
        // logger.debug("method1 : called. arg :  {}" , abc );
        Customer customerLoc = new Customer("osman","yaycıoğlu");
        // logger.debug("method1 : finished. arg :  " + abc );
    }

    public void method2(String str,int year){
        if (year > 2010){
            System.out.println(str);
        } else {
            System.out.println("eski");
        }
    }
}

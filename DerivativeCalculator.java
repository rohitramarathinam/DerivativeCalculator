import java.util.*;
public class DerivativeCalculator {
    private ArrayList<String> terms;
    public DerivativeCalculator() { resetTerms(); }

    public void resetTerms() { this.terms = new ArrayList<String>(); }

    private String parenthesize(String s) { return "(" + s + ")"; }

    private String powerRule(int c, String x, int e) {
        //c = 1, x = x, e = 2 = x^2 -> d/dx[x^2] -> 2x
        if (e==0) return e+"";
        String res = (c*e)+"";
        if ((c*e)>0) res = "+" + (c*e);
        if (e!=1) {
            res += x;
            if (e>2) res += "^" + (e-1); 
        }
        return res;
    }

    public String powerRule(String str) {
        String finalRes = "";
        scanEquation(str);
        for (String s : terms) {
            String help = analyzeTerm(s);
            if (help.equals("")) break;
            int c = Integer.parseInt(help.substring(0, help.indexOf(",")));
            help = help.substring(help.indexOf(",")+1);
            String x = help.substring(0, help.indexOf(","));
            help = help.substring(help.indexOf(",")+1);
            finalRes += powerRule(c, x, Integer.parseInt(help));
        }
        if (finalRes.equals("")) finalRes = "0";
        if (finalRes.charAt(0)=='+') finalRes = finalRes.substring(1);
        return finalRes;
    }

    public String simplify(int c1, String x, int e1, int c2, int e2) {
        //String res = 
        return multiply(c1, x, e1, c2, e2);
    }

    private String multiply(int c1, String x, int e1, int c2, int e2) { return (c1*c2) + x + "^" + (e1+e2); }

    //private String add(int c1, int c2) { return c1}

    public String productRule(String str) {
        int c1 = 0; int c2 = 0; int e1 = 0; int e2 = 0;
        int lbctr = 0; int rbctr = 0;
        for (char c : str.toCharArray()) {
            if (c=='(') lbctr++;
            else if (c==')') rbctr++;
        }
        if (lbctr==2 && rbctr==2) { //is in (f)(g) form
            scanEquation(str.substring(1, str.indexOf(")")));
            String k = analyzeTerm(terms.get(0));
            c1 = Integer.parseInt(k.valueOf(k.charAt(0))); e1 = Integer.parseInt(k.valueOf(k.charAt(k.length()-1)));
            resetTerms();
            scanEquation(str.substring(str.indexOf(")") + 2, str.length()-1)); 
            k = analyzeTerm(terms.get(0));
            c2 = Integer.parseInt(k.valueOf(k.charAt(0))); e2 = Integer.parseInt(k.valueOf(k.charAt(k.length()-1)));
        }
        return productRule(c1, "x", e1, c2, e2);
    }

    private String productRule(int c1, String x, int e1, int c2, int e2) {
        //eq = (3x^2)(2x^3)
        String pr = powerRule(c1, x, e1);
        if (pr.charAt(0)=='+') pr = pr.substring(1);
        String res = "(" + pr + ")(";
        if (c2>1) res+=c2; 
        res+= x;
        if (e2>1) res+= "^" + e2;
        res+= ")";
        res+= " + ("; 
        if (c1>1) res+=c1; 
        res+= x;
        if (e1>1) res+= "^" + e1;
        res+= ")";
        pr = powerRule(c2, x, e2);
        if (pr.charAt(0)=='+') pr = pr.substring(1);
        res+= "(" + pr + ")";
        return res;
    }

    public String quotientRule(String str) {
        String f = str.substring(0, str.indexOf('/'));
        String g = str.substring(str.indexOf('/') + 1);
        scanEquation(f); String k = "";
        int c1 = 0; int e1 = 0; int c2 = 0; int e2 = 0;
        k = analyzeTerm(terms.get(0));
        c1 = Integer.parseInt(k.valueOf(k.charAt(0))); e1 = Integer.parseInt(k.valueOf(k.charAt(k.length()-1)));
        resetTerms();
        scanEquation(g); 
        k = analyzeTerm(terms.get(0));
        c2 = Integer.parseInt(k.valueOf(k.charAt(0))); e2 = Integer.parseInt(k.valueOf(k.charAt(k.length()-1)));
        return quotientRule(c1, "x", e1, c2, e2);
    }

    private String quotientRule(int c1, String x, int e1, int c2, int e2) {
        //(f'g - g'f)/g^2
        String prf = powerRule(c1, x, e1) + ")";
        String prg = powerRule(c2, x, e2) + ")";
        if (prf.charAt(0)=='+') prf = "(" + prf.substring(1);
        if (prg.charAt(0)=='+') prg = "(" + prg.substring(1);
        String num = prf + ("(" + c2 + x + "^" + e2 + ")") + " - " + ("(" + c1 + x + "^" + e1 + ")") + prg;
        String denom = multiply(c2, x, e2, c2, e2);
        return "[" + num + "]" + " / [" + denom + "]";   
    }

    public String naturalLog(int c, String x) { return "ln(" + c + x + ")"; }

    public String chainRule(String str) {
        //f(x) = 2(x+1)^2
        String u = "";
        if (str.contains("(") && str.contains(")")) {
            u = str.substring(str.indexOf("(")+1, str.indexOf(")")); //lastIndexOf(")")); 
            str = str.substring(0, str.indexOf("(")) + "u" + str.substring(str.indexOf(")")+1);
        }
        String finalRes = powerRule(str);
        resetTerms();
        String temp = powerRule(u);
        if (!(temp.equals("1"))) finalRes += "(" + temp + ")";
        finalRes = finalRes.substring(0, finalRes.indexOf("u")) + parenthesize(u) + finalRes.substring(finalRes.indexOf("u")+1);
        return finalRes;
    }

    public static void main(String[] args) {
        openingMessage();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter equation to derive:\nf(x) = ");
        String str = scanner.nextLine();
        System.out.print("\nEnter derivative rule to use : ");
        String r = scanner.nextLine();
        r = r.toLowerCase();
        DerivativeCalculator dc = new DerivativeCalculator();
        if (r.equals("power rule"))
            System.out.println("\nf'(x) = " + dc.powerRule(str));
        else if (r.equals("chain rule"))
            System.out.println("\nf'(x) = " + dc.chainRule(str));
        else if (r.equals("product rule"))
            System.out.println("\nf'(x) = " + dc.productRule(str)); //System.out.println("\nf(x) = " + dc.simplify(2, "x", 1, 2, 1));
        else if (r.equals("quotient rule"))
            System.out.println("\nf'(x) = " + dc.quotientRule(str));
        else
            System.out.println("\nInvalid input maybe?");
    }
    
    private static void openingMessage() {
        Scanner console = new Scanner(System.in);
        System.out.println("1. product rule is space sensitive, no spaces in between please\n2. Please use simple inputs for product and quotient rules for now\n\t - Ex. -> (2x^3)(3x^2)\n\t - Ex. -> x^2 / 3x^3\n3. Still trying to fix some stuff so ya bare with me :D");
        System.out.println("\nEnter any input to continue : ");
        console.nextLine();
        System.out.print('\u000c');
    }

    public void scanEquation(String str) {
        String temp = "";
        while (str.length()>0) {   //2(x+1)^2           
            int index = -1;
            if (str.indexOf("-")!=-1 && str.indexOf("+")!=-1) {
                if (str.indexOf("-") < str.indexOf("+")) index = str.indexOf("-");
                if (str.indexOf("+") < str.indexOf("-")) index = str.indexOf("+");
            }
            else if (str.indexOf("-")!=-1 && str.indexOf("+")==-1) index = str.indexOf("-");
            else if (str.indexOf("-")==-1 && str.indexOf("+")!=-1) index = str.indexOf("+");
            if (str.contains("+") || str.contains("-")) {
                if (str.charAt(0)=='+' || str.charAt(0)=='-') {
                    temp = ""+str.charAt(0);
                    str = str.substring(1);
                    continue;
                }
                terms.add(temp + str.substring(0, index));
                str = str.substring(index);
                temp = "";
            }
            else {
                terms.add(temp + str);
                break;
            }
        }
    }

    public String analyzeTerm(String s) {
        int run = 0;
        String c = ""; String x = ""; String e = "";
        while (s.length()>0) {
            if (s.charAt(0)==' ' || s.charAt(0)=='+') { 
                s = s.substring(1); 
                continue;
            }
            if (s.charAt(0)=='-' || (s.charAt(0)>=49 && s.charAt(0)<=57)) {
                c+=""+s.charAt(0);
                s = s.substring(1);
            }
            else if (run==0) c = "1";
            else if (s.charAt(0)=='x' || s.charAt(0)=='u') {
                x+=""+s.charAt(0);
                if (s.length()>0) s = s.substring(1);
            }
            else if (s.charAt(0)=='^') {
                s = s.substring(1);
                if (s.charAt(0)=='-') e+="-";
                while (s.length()>0 && s.charAt(0)>=49 && s.charAt(0)<=57) {
                    e+=""+s.charAt(0);
                    if (s.length()>0) s = s.substring(1);
                    else break;
                }
            }
            run++;
        }
        if (c.equals("-")) c = "-1";
        if (x.equals("")) return "";
        if (e.equals("")) e = "1";
        return c + "," + x + "," + e;
    }
}
import java.util.*;

public class Main {
    public static void main(String[] args) {

        HashMap<String, Integer> valueOfOperations = new HashMap<>();
        valueOfOperations.put("\\to", 1);
        valueOfOperations.put("\\leftrightarrow", 1);
        valueOfOperations.put("\\land", 2);
        valueOfOperations.put("\\lor", 2);
        valueOfOperations.put("\\otimes", 2);
        valueOfOperations.put("\\lnot", 3);
        valueOfOperations.put("(", -1);



        Scanner input = new Scanner(System.in);
        String in = input.nextLine().trim();
        String[] elements = in.split(" ");
        //System.out.println(Arrays.toString(elements))   ;
        HashMap<String, Boolean> variables = new HashMap<>();
        for (String value : elements) {
            //System.out.println(elements[i]);
            if (value.charAt(0) != '\\' && !value.equals("(") && !value.equals(")"))
                variables.put(value, false);
        }
        Stack<String> number = new Stack<>();
        Stack<String> operation = new Stack<>();
        LinkedList<String> operationWithNumber = new LinkedList<>();
        LinkedList<String> lastList = new LinkedList<>();

        for (String element : elements) {
            //System.out.println(operation);
            //for(String o :operation){
            //System.out.printf("%s ", o);
            //}
            if (element.equals(")")) {
                String lastOp = operation.peek();
                while (!lastOp.equals("(")) {
                    operation.pop();
                    lastList.add(lastOp);
                    String combine;
                    if (lastOp.equals("\\lnot")) {
                        String lastNumber = number.pop();
                        combine = "\\lnot " + lastNumber;
                    } else {
                        String lastNumber = number.pop();
                        String lastTwoNumber = number.pop();
                        combine = lastTwoNumber + " " + lastOp + " " + lastNumber;
                    }
                    operationWithNumber.add(combine);
                    number.push(combine);
                    lastOp = operation.peek();
                }
                //System.out.println(number);
                operation.pop();
                String tmp = operationWithNumber.pollLast();
                operationWithNumber.add("(" + tmp + ")");
                number.pop();
                number.push("(" + tmp + ")");

            } else if (element.equals("(")) {
                operation.push(element);
            } else if (element.charAt(0) == '\\') { // 是运算符
                if (operation.size() != 0) {
                    String lastOp = operation.peek();
                    while (valueOfOperations.get(lastOp) >= valueOfOperations.get(element)) {
                        operation.pop();
                        lastList.add(lastOp);
                        String combine;
                        if (lastOp.equals("\\lnot")) {
                            String lastNumber = number.pop();
                            combine = "\\lnot " + lastNumber;
                        } else {
                            String lastNumber = number.pop();
                            String lastTwoNumber = number.pop();
                            combine = lastTwoNumber + " " + lastOp + " " + lastNumber;
                        }
                        operationWithNumber.add(combine);
                        number.push(combine);
                        if (operation.isEmpty()) break;
                        lastOp = operation.peek();
                    }
                }
                operation.push(element);
            } else { // 是数字
                number.push(element);
                lastList.add(element);
            }
        }
        //.out.println(operation.size());
        //System.out.println(operationWithNumber);
        while(!operation.isEmpty()){
            String tmp = operation.pop();
            lastList.add(tmp);
            String combine;
            if(tmp.equals("\\lnot")){
                String lastNumber = number.pop();
                combine = "\\lnot " + lastNumber;
            }else{
                String lastNumber = number.pop();
                String lastTwoNumber = number.pop();
                combine = lastTwoNumber + " " + tmp + " " + lastNumber;
                //System.out.println(combine);
            }
            operationWithNumber.add(combine);
            number.add(combine);
        }
        //for(String s : lastList) System.out.println(s);

       // System.out.println(lastList);
        //System.out.println(operationWithNumber);
        ArrayList<String> var = new ArrayList<>(variables.keySet());
        int totalVar = 1 << var.size();
        int betweenOperation = operationWithNumber.size();
        StringBuilder table = new StringBuilder();
        table.append('|');
        for(String s :var){
            table.append('$').append(s).append("$|");
        }
        for(String s : operationWithNumber){
            if(s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')' && s.indexOf("(", 1) == -1){
                table.append('$').append(s, 1, s.length() - 1).append("$|");
            }
            else table.append('$').append(s).append("$|");
        }
        table.append("\n|");
        table.append(":-:|".repeat(betweenOperation + var.size()));
        table.append('\n');
        //System.out.println(lastList);
        for(int index = 0; index < totalVar; index++){
            int tmp = index;
            for(int i = var.size() - 1; i >= 0; i--){
                variables.put(var.get(i), (tmp & 1) != 0);
                tmp = tmp >> 1;
            }
            table.append('|');
            for(int i = 0; i < var.size(); i++){
                table.append(variables.get(var.get(i)) ? 'T' : 'F').append('|');
            }
            Stack<Boolean> cal = new Stack<>();
            for(String s : lastList){
                //System.out.println(cal);
                if(s.charAt(0) == '\\'){
                    boolean result = false;
                    if(s.equals("\\lnot")){
                        boolean t = cal.pop();
                        result = !t;
                    }else if(s.equals("\\land")){
                        boolean t1 = cal.pop();
                        boolean t2 = cal.pop();
                        result = t2 && t1;
                    }else if(s.equals("\\lor")){
                        boolean t1 = cal.pop();
                        boolean t2 = cal.pop();
                        result = t2 || t1;
                    }else if(s.equals("\\otimes")){
                        boolean t1 = cal.pop();
                        boolean t2 = cal.pop();
                        result = t2 ^ t1;
                    }else if(s.equals("\\to")){
                        boolean t1 = cal.pop();
                        boolean t2 = cal.pop();
                        result = !t2 || t1;
                    }else if(s.equals("\\leftrightarrow")){
                        boolean t1 = cal.pop();
                        boolean t2 = cal.pop();
                        result = t1 == t2;
                    }
                    cal.push(result);
                    table.append(result ? 'T' : 'F').append('|');
                }else{
                    cal.push(variables.get(s));
                }
            }
            table.append('\n');
        }
        System.out.println(table);
    }
}

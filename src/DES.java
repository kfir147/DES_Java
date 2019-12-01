//that's how you return it to a string from binary.
//String get_msg = new String(new BigInteger(binary_msg, 2).toByteArray());
//System.out.println("as text: "+get_msg);

import java.math.BigInteger;
import java.util.ArrayList;

public class DES {

    public String Cipher(String msg, String key)
    {

        // Part 1 - Key generation
        KeyGeneration keyProccess = new KeyGeneration();
        String binaryKey =  keyProccess.ConvertBinary(key);
        //System.out.println("Binary key is: " +binaryKey);
        String pcKey = keyProccess.PC(binaryKey,1);
        ArrayList <String> keys_list = keyProccess.split_and_round(pcKey);
        System.out.println("Keys created are: " + keys_list);

        ArrayList<String> packages = make_packages(msg);
        ArrayList<String> encrypted_packages = new ArrayList<>();


        //TESTS (to view the packages themselves)
        for (int i=0;i<packages.size();i++)
        {
            System.out.println("package "+ i +": "+ packages.get(i));
        }


        //sending every single package into encryption
        for(int i = 0; i < packages.size(); i++)
        {
            encrypted_packages.add(encrypt(packages.get(i), keys_list));
        }

        //END PART - grab together all of the packges.
        String cypher_msg = "";
        for (int i = 0; i < packages.size(); i++)
            cypher_msg += packages.get(i);
        System.out.println("the long binary is: " + cypher_msg);

        //DELETE the remaining zeros from the binary code.
        while (cypher_msg.charAt(cypher_msg.length() - 1) == '0')
            cypher_msg = cypher_msg.substring(0, cypher_msg.length() - 1);

        //Convert the binary to String.
        String get_msg = new String(new BigInteger(cypher_msg, 2).toByteArray()).toString();
        System.out.println("the back together message is: " + get_msg);

        //System.out.println("the encripted msg is: "+msg);


        return get_msg;
    }


    //a function that splits the message into packages of 64bit each.
    private ArrayList<String> make_packages(String msg){

        //TESTS
        System.out.println("the original msg is: "+msg);

        //an array list for all the pieces of the package
        ArrayList<String> packages = new ArrayList<>();

        //convert the original message to binary code.
        String binary_msg = new BigInteger(msg.getBytes()).toString(2);

        //TESTS
        System.out.println("as binary: "+ binary_msg);
        System.out.println("binary length: "+ binary_msg.length());

        //cutting the message into 64 bits each
        while (binary_msg.length() > 64)
        {
            packages.add(binary_msg.substring(0,64));
            binary_msg = binary_msg.substring(64);
        }


        //for the last package (if it exist), I am filling it with many zeros at the end.
        // in order to make it also 64 bit.
        if (binary_msg.length() > 0)
        {
            if (binary_msg.length() < 64) {
                String adder = new String(new char[64 - binary_msg.length()]).replace('\0', '0');
                binary_msg = binary_msg.concat(adder);
            }
            packages.add(binary_msg);
        }


        return packages;
    }

    private String s_boxes(int index, String s){
        int[] sbox1 = { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14,
                8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9,
                1, 7, 5, 11, 3, 14, 10, 0, 6, 13 };
        int[] sbox2 = { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7,
                11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3,
                15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 };
        int[] sbox3 = { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4,
                9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9,
                8, 7, 4, 15, 14, 3, 11, 5, 2, 12 };
        int[] sbox4 = { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9,
                0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1,
                13, 8, 9, 4, 5, 11, 12, 7, 2, 14 };
        int[] sbox5 = { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1,
                11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1,
                14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 };
        int[] sbox6 = { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14,
                15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9,
                5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 };
        int[] sbox7 = { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11,
                13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4,
                10, 7, 9, 5, 0, 15, 14, 2, 3, 12 };
        int[] sbox8 = { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4,
                1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10,
                8, 13, 15, 12, 9, 0, 3, 5, 6, 11 };

        return null;
    }

    private String encrypt(String msg, ArrayList<String> keys_list){


        //TEST to check that IP and IPInvert works
        msg = ip(msg);
        System.out.println("msg stage 1: " + msg + "\n");
        msg = ipInverse(msg);
        System.out.println("msg stage 2: " + msg + "\n");

        String mL = "";
        String mR = "";
        String new_mL = "";
        String current_key = "";

        msg = ip(msg);

        msg = ip(msg);

        System.out.println("msg stage 1: " + msg + "\n\n");


        for (int i = 0; i<16; i++) {
            mL = msg.substring(0, 32);
            mR = msg.substring(32);

            //
            System.out.println("mR \t" + mR);
            System.out.println("keys_list \t" + keys_list.get(i));
            //
            current_key = The_F_Function(mR, keys_list.get(i));

            //
            System.out.println("current_key \t" + current_key);
            //
            new_mL = XOR(mL,current_key);

            msg = "" + mR + new_mL;
        }
        System.out.println("msg stage 2: " + msg + "\n\n");


        msg = ipInverse(msg);

        //TESTS
        System.out.println("R= \t" + mL);
        System.out.println("L= \t" + mR);
        System.out.println("***************************************************************8 "+msg);

        String get_msg = new String(new BigInteger(msg, 2).toByteArray());
        System.out.println("as text:\n "+get_msg);
        return msg;
    }


    private String decrypt(String msg, String key){

        ArrayList<String> packages = new ArrayList<>();




        //END PART - grab together all of the packges.
        String cypher_msg = "";
        for (int i = 0; i < packages.size(); i++)
            cypher_msg += packages.get(i);
        System.out.println("the long binary is: " + cypher_msg);

        //DELETE the remaining zeros from the binary code.
        while (cypher_msg.charAt(cypher_msg.length() - 1) == '0')
            cypher_msg = cypher_msg.substring(0, cypher_msg.length() - 1);

        //Convert the binary to String.
        String get_msg = new String(new BigInteger(cypher_msg, 2).toByteArray()).toString();
        System.out.println("the back together message is: " + get_msg);

        //System.out.println("the encripted msg is: "+msg);

        return get_msg;
    }

    //initial Permutation, input should be binary string
    private String ip(String input){
        int[] ip =
                {
                        58, 50, 42, 34, 26, 18, 10 , 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14,
                        6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53,
                        45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
                };

        String msg = "";

        char [] MsgArray = input.toCharArray();
        for (int i=0; i<ip.length; i++)
            msg += MsgArray[ip[i]-1];
        return msg;
    }

    //final Permutation, input should be binary string
    private String ipInverse(String input){
        int[] ip =
                {
                        40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
                        36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43 ,11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25
                };

        String msg = "";
        char [] MsgArray = input.toCharArray();

        for (int i=0; i<ip.length; i++)
            msg += MsgArray[ip[i]-1];

        return msg;
    }

    //Expansion E - expand the string from 32 to 48 byts
    private String E(String input){
        int[] e =
                {
                        32,  1,  2,  3,  4,  5, 4,  5,  6,  7,  8,  9, 8,  9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
                        16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32,  1
                };

        String res = "";
        char [] MsgArray = input.toCharArray();
        for (int i=0; i<e.length; i++)
            res += MsgArray[e[i]-1];

        return res;
    }

    //function that perform xor operator on 2 binary strings
    private String XOR (String msg, String Key)
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < msg.length(); i++)
            sb.append(msg.charAt(i)^Key.charAt(i));

        return sb.toString();
    }


    // F function inculde 4 stages init: 1.Expansion E, 2.XOR with round key, 3.S boxes, 4.Permutation
    private String The_F_Function (String MsgRight, String CurrentKey)
    {
        //Step 1 - Expansion E
        String ExpandMsg= E(MsgRight);
        System.out.println("String after function E:  " + ExpandMsg);
        //Step 2 - Xor with the key
        String XorMsg = XOR(ExpandMsg,CurrentKey);
        System.out.println("String after XOR: " +XorMsg);

        //step 3 - S boxes
        //cutting the message to packets of 6 byts
        ArrayList <String> Sbox_Package = new ArrayList<>();
        while (XorMsg.length() > 64)
        {
            Sbox_Package.add(XorMsg.substring(0,64));
            XorMsg = XorMsg.substring(64);
        }


        return XorMsg;
    }

}

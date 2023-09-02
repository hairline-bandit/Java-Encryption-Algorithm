import java.util.Scanner;
import java.util.Random;
import java.lang.Integer;
import java.util.Arrays;

/*
Key:
    Cycle Bytes
    Cycle Rows
    Cycle Columns
Message:
    Nibbles xor + 1st nibble
Then Combine.
*/
public class Encryptor {
    static int rounds = 20;
    static String xor(String byt, String byt2) {
        String out = "";
        for (int i = 0; i < byt.length(); i++) {
            if (("" + byt.charAt(i)).equals("" + byt2.charAt(i))){
                out += "0";
            } else {
                out += "1";
            }
        }
        return out;
    }

    static String[] key_gen() {
        Random r = new Random();
        String[] out = new String[64];
        for (int i = 0; i < 64; i++) {
            String temp = Integer.toBinaryString(r.nextInt(256));
            String out2 = "";
            for (int j = 0; j < 8 - temp.length(); j++) {
                out2 += "0";
            }
            out[i] = out2 + temp;
        }
        return out;
    }

    static String[][] matrixer(String[] list) {
        String[][] out = new String[8][8];
        int count = 0;
        for(int i = 0; i < 64; i += 8) {
            out[count] = Arrays.copyOfRange(list, i, i+8);
            count++;
        }
        return out;
    }

    static String bin_to_hex(String bin) {
        int num = Integer.parseInt(bin, 2);
        String temp = Integer.toHexString(num);
        String out = "";
        if (temp.length() < 2) {
            for (int i = 0; i < 2 - temp.length(); i++) {
                out += "0";
            }
        }
        out += temp;
        return out;
    }

    static String hex_to_bin(String hex) {
        int num = Integer.parseInt(hex, 16);
        String temp = Integer.toBinaryString(num);
        String out = "";
        if (temp.length() < 8) {
            for (int i = 0; i < 8 - temp.length(); i++) {
                out += "0";
            }
        }
        out += temp;
        return out;
    }

    static String int_to_bin(char character) {
        int c = (int)character;
        String temp = Integer.toBinaryString(c);
        String out = "";
        if (temp.length() < 8) {
            for (int i = 0; i < 8 - temp.length(); i++) {
                out += "0";
            }
        }
        out += temp;
        return out;
    }

    static String pad(String message) {
        String out = message;
        if (message.length() > 64) {
            System.out.println("Message has too many characters");
            System.exit(0);
        } else {
            int missing = 64 - out.length();
            for (int i = 0; i < missing; i++) {
                out += "`";
            }
        }
        return out;
    }

    static String[] shift_array(String[] array, int shiftLength) {
        String[] out = new String[8];
        for (int i = 0; i < 8; i++) {
            out[(i + shiftLength) % 8] = array[i];
        }
        return out;
    }

    static String shift_byte(String byt, int shiftLength) {
        String[] out = new String[8];
        for (int i = 0; i < 8; i++) {
            out[(i + shiftLength) % 8] = "" + byt.charAt(i);
        }
        return String.join("", out);
    }

    static void bin_print_single(String[] array) {
        String out = "";
        for (String i : array) {
            out += bin_to_hex(i);
        }
        System.out.println(out);
    }

    static void bin_print_double(String[][] matrix) {
        String out = "";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                out += bin_to_hex(matrix[i][j]);
            }
        }
        System.out.println(out);
    }
    
    static String de_pad(String msg) {
        String out = "";
        for (int i = 0; i < msg.length(); i ++) {
            out += msg.charAt(i) == '`' ? "" : "" + msg.charAt(i);
        }
        return out;
    }

    static void pprint(String[][] msg) {
        String out = "";
        for (int i = 0; i < msg.length; i++) {
            for (int j = 0; j < msg[i].length; j++) {
                int temp = Integer.parseInt(msg[i][j], 2);
                out += "" + (char)temp;
            }
        }
        System.out.println(de_pad(out));
    }

    static String[][] round_gen(String[][] org_key, int round) {
        String[][] u = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                u[i][j] = org_key[i][j];
            }
        }
        for (int r = 0; r < round + 1; r++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    u[i][j] = shift_byte(u[i][j], j);
                }
            }
            for (int i = 0; i < 8; i++) {
                u[i] = shift_array(u[i], i);
            }
            for (int i = 0; i < 8; i++) {
                String[] temp = new String[8];
                for (int j = 0; j < 8; j++) {
                    temp[j] = u[j][i];
                }
                temp = shift_array(temp, i);
                for (int j = 0; j < 8; j++) {
                    u[j][i] = temp[j];
                }
            }
        }
        return u;
    }

    static void encrypt() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Generate a key (1) or use your own (2): ");
        String choice = scan.nextLine();

        String[] key = new String[64];
        if (choice.equals("1")) {
            key = key_gen();
            System.out.println("Your key is:");
            bin_print_single(key);
        } else if (choice.equals("2")) {
            System.out.print("Enter your key: ");
            String inp = scan.nextLine();
            if (inp.length() < 128) {
                System.out.println("Key too small");
                System.exit(0);
            }
            int count = 0;
            for (int i = 0; i < inp.length(); i += 2) {
                key[count] = hex_to_bin(inp.substring(i, i+2));
                count++;
            }
        }
        String[][] matrix_key = matrixer(key);
        String[][] matrix_message;
        String message;
        System.out.print("Enter your message: ");
        message = scan.nextLine();
        message = pad(message);
        String[] message_array = new String[64];
        for (int i = 0; i < message.length(); i++) {
            message_array[i] = int_to_bin(message.charAt(i));
        }
        matrix_message = matrixer(message_array);
        System.out.print("Enter the number of rounds or 0 for the default of 20: ");
        int tmpr = scan.nextInt();
        if (tmpr < 0) {
            System.exit(1);
        } else if (tmpr != 0) {
            rounds = tmpr;
        }
        // Combine key and message
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                matrix_message[i][j] = xor(matrix_message[i][j], matrix_key[i][j]);
            }
        }
        
        // Round function
        for (int r = 0; r < rounds; r++) {
            // Fiddle with bytes and nibbles and such
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    matrix_message[i][j] = xor(matrix_message[i][j].substring(0, 4), matrix_message[i][j].substring(4)) + matrix_message[i][j].substring(0, 4);
                }
            }
            // Rotate bytes
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    matrix_key[i][j] = shift_byte(matrix_key[i][j], j);
                }
            }
            // Rotate rows
            for (int i = 0; i < 8; i++) {
                matrix_key[i] = shift_array(matrix_key[i], i);
            }
            // Rotate Columns
            for (int i = 0; i < 8; i++) {
                String[] temp = new String[8];
                for (int j = 0; j < 8; j++) {
                    temp[j] = matrix_key[j][i];
                }
                temp = shift_array(temp, i);
                for (int j = 0; j < 8; j++) {
                    matrix_key[j][i] = temp[j];
                }
            }
            // Combine
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    matrix_message[i][j] = xor(matrix_message[i][j], matrix_key[i][j]);
                }
            }
        }
        System.out.println("Your message:");
        bin_print_double(matrix_message);
        scan.close();
    }

    static void decrypt() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the encrypted message: ");
        String message = scan.nextLine();
        System.out.print("Enter the key: ");
        String key = scan.nextLine();
        System.out.print("Enter the number of rounds or 0 for the default of 20: ");
        int tmpr = scan.nextInt();
        if (tmpr < 0) {
            System.exit(1);
        } else if (tmpr != 0) {
            rounds = tmpr;
        }
        String[] arrayMsg = new String[64];
        String[] arrayKey = new String[64];
        String[] temp1 = new String[64];
        int counter = 0;
        for (int i = 0; i < 128; i += 2) {
            arrayMsg[counter] = hex_to_bin(message.substring(i, i+2));
            arrayKey[counter] = hex_to_bin(key.substring(i, i+2));
            temp1[counter] = hex_to_bin(key.substring(i, i+2));
            counter++;
        }

        String[][] matrix_message = matrixer(arrayMsg);
        String[][] matrix_key = matrixer(arrayKey);
        
        // Round function
        for (int r = rounds - 1; r >= 0; r--) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    matrix_message[i][j] = xor(matrix_message[i][j], round_gen(matrix_key, r)[i][j]);
                }
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    matrix_message[i][j] = matrix_message[i][j].substring(4) + xor(matrix_message[i][j].substring(0, 4), matrix_message[i][j].substring(4));
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                matrix_message[i][j] = xor(matrix_message[i][j], matrix_key[i][j]);
            }
        }
        System.out.println("Your message:");
        pprint(matrix_message);
        scan.close();
    }
    public static void main(String args[]) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Encrypt (1) or decrypt (2): ");
        String choice = scan.nextLine();
        if (choice.equals("1")) {
            encrypt();
        } else if (choice.equals("2")) {
            decrypt();
        } else {
            System.exit(0);
        }
        scan.close();
    }
}
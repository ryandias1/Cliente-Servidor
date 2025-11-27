package br.com.ibm.intelimed.network;

import java.io.*;

/*
    Classe Teclado serve para facilitar a digitação de dados pelos usuarios usando o BufferedReader
    Essa classe permite receber dados de todos os tipos e trata cada excessão
    Chamada de Exemplo: String palavra = Teclado.getUmString();
*/

public class Teclado {
    private static final BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

    public static String getUmString() {
        String retorno = null;
        try {
            retorno = teclado.readLine();
        } catch (IOException erro) {
            //Sem chances de erro
        }
        return retorno;
    }

    public static byte getUmByte() throws Exception {
        byte retorno = (byte) 0;
        try {
            retorno = Byte.parseByte(teclado.readLine());
        } catch (IOException erro) {
            //Sem chances de erro
        } catch (NumberFormatException erro) {
            throw new Exception("Byte invalido!");
        }
        return retorno;
    }

    public static short getUmShort() throws Exception {
        short retorno = (short) 0;
        try {
            retorno = Short.parseShort(teclado.readLine());
        } catch (IOException erro) {
            //Sem chances de erro
        } catch (NumberFormatException erro) {
            throw new Exception("Short invalido!");
        }
        return retorno;
    }

    public static int getUmInt() throws Exception {
        int retorno = 0;
        try {
            retorno = Integer.parseInt(teclado.readLine());
        } catch (IOException erro) {
            //Sem chances de erro
        } catch (NumberFormatException erro) {
            throw new Exception("Int invalido!");
        }
        return retorno;
    }

    public static long getUmLong() throws Exception {
        long retorno = 0L;
        try {
            retorno = Long.parseLong(teclado.readLine());
        } catch (IOException erro) {
            //Sem chances de erro
        } catch (NumberFormatException erro) {
            throw new Exception("Long invalido!");
        }
        return retorno;
    }

    public static float getUmFloat() throws Exception {
        float retorno = 0.0f;
        try {
            retorno = Float.parseFloat(teclado.readLine());
        } catch (IOException erro) {
            //Sem chance de erro
        } catch (NumberFormatException erro) {
            throw new Exception("Float invalido!");
        }
        return retorno;
    }

    public static double getUmDouble() throws Exception {
        double retorno = 0.0;
        try {
            retorno = Double.parseDouble(teclado.readLine());
        } catch (IOException erro) {
            //Sem chances de erro
        } catch (NumberFormatException erro) {
            throw new Exception("Double invalido!");
        }

        return retorno;
    }

    public static char getUmChar() throws Exception {
        char retorno = ' ';
        try {
            String str = teclado.readLine();
            if (str==null) {
                throw new Exception("Char invalido!");
            }
            if (str.length() != 1) {
                throw new Exception("Char invalido!");
            }
            retorno = str.charAt(0);
        } catch (IOException erro) {
            //Sem chances de erro
        }

        return retorno;
    }

    public static boolean getUmBoolean() throws Exception {
        boolean retorno = false;
        try {
            String bool = teclado.readLine();
            if (bool==null) {
                throw new Exception("Boolean invalido!");
            }
            if (!bool.equals("false") && !bool.equals("true")) {
                throw new Exception("Boolean invalido!");
            }
            retorno = Boolean.parseBoolean (bool);
        } catch (IOException erro) {
            //Sem chance de erro
        }
        return retorno;
    }
}

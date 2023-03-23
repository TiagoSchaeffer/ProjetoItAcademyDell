package org.projeto.dell;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que realiza as funcionalidades do programa da DELL
 *
 * @author Tiago M. Schaeffer S.
 */
public class Programa {
    /**
     *  Váriavel estática com o preço do caminhão pequeno
     */
    static double CAMINHAO_PEQUENO = 4.87;
    /**
     *  Váriavel estática com o preço do caminhão médio
     */
    static double CAMINHAO_MEDIO = 11.92;

    /**
     *  Váriavel estática com o preço do caminhão grande
     */
    static double CAMINHAO_GRANDE = 27.44;


    public void printLista() {
        List<List<String>> lista = criarMatrix();

        for (List<String> strings : lista) {
            for (String string : strings) System.out.print(string + " ");
            System.out.println();
        }
    }

    /**
     *  Método com a finalidade de criar a matrix com o csv, para manipular
     *  os dados.
     *
     * @return Retorna uma matrix de String, feita com List Collection.
     */
    List<List<String>> criarMatrix() {
        String path = "src\\main\\resources\\DNIT-Distancias.csv";
        List<List<String>> lista = new ArrayList<List<String>>();
        String line;

        try (BufferedReader dataxls = new BufferedReader(new FileReader(path))) {

            while ((line = dataxls.readLine()) != null) {
                List<String> linha = new ArrayList<>();
                String[] line_split = line.split(";");

                Collections.addAll(linha, line_split);

                lista.add(linha);
            }
            return lista;
        }
        catch (IOException e) {
            e.printStackTrace();
            return lista;
        }
    }


    int buscarIndexCidade(String cidade) {
        List<List<String>> lista = criarMatrix();
        for (int i = 0; i < lista.get(0).size(); i++) {
            if (lista.get(0).get(i).equalsIgnoreCase(cidade))
                return i;
        }
        return -1;
    }
}

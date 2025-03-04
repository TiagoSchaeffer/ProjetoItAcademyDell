package org.projeto.dell;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe que realiza as funcionalidades do programa da DELL.
 *
 * @author Tiago M. Schaeffer S.
 */
public class Programa {

    /**
     * Váriavel estática com o preço do caminhão pequeno
     */
    private static final double CAMINHAO_PEQUENO = 4.87;
    /**
     * Váriavel estática com o preço do caminhão médio
     */
    private static final double CAMINHAO_MEDIO = 11.92;
    /**
     * Váriavel estática com o preço do caminhão grande
     */
    private static final double CAMINHAO_GRANDE = 27.44;

    /**
     * Atributo com todos os cadastros feitos.
     */
    List<Transporte> cadastrosTransportes = new ArrayList<>();

    /**
     * Método com a finalidade de criar a matriz com o csv, para manipular
     * os dados.
     *
     * @return Retorna uma matriz de String, feita com List Collection.
     */
    public List<List<String>> criarMatriz() {
        String caminho = "src\\main\\resources\\DNIT-Distancias.csv";
        List<List<String>> lista = new ArrayList<>();
        try (BufferedReader dataxls = new BufferedReader(new FileReader(caminho))) {
            String line;
            while ((line = dataxls.readLine()) != null) {
                List<String> linha = new ArrayList<>();
                String[] listaLinha = line.split(";");
                Collections.addAll(linha, listaLinha);
                lista.add(linha);
            }
            return lista;
        } catch (IOException e) {
            e.printStackTrace();
            return lista;
        }
    }

    /**
     * Método para procurar a index apartir do nome da cidade.
     *
     * @param cidade qual cidade procurar.
     * @return Retorna um int com o index da cidade.
     */
    public int buscarIndexCidade(String cidade) {
        List<List<String>> lista = criarMatriz();
        for (int i = 0; i < lista.get(0).size(); i++) {
            if (lista.get(0).get(i).equalsIgnoreCase(cidade))
                return i;
        }
        return -1;
    }

    /**
     * Método para consultar o preço e a distância entre duas cidades, com base
     * no tipo de caminhão.
     *
     * @param cidadeI    String cidade inicial
     * @param cidadeF    String cidade final
     * @param modalidade int com o tipo de caminhão, sendo 0 - pequeno, 1 - médio e 2 - grande.
     * @return Retorna uma lista de double com o valor [0] sendo a distância, e o [1] sendo o custo.
     */
    public List<Double> consultarTrechosxModalidade(String cidadeI, String cidadeF, int modalidade) {
        List<List<String>> lista = criarMatriz();
        int indexI = buscarIndexCidade(cidadeI);
        int indexF = buscarIndexCidade(cidadeF);
        double distancia = Integer.parseInt(lista.get(indexI + 1).get(indexF));
        double custo = 0.0;
        if (modalidade == 0) {
            custo = CAMINHAO_PEQUENO * distancia;
        } else if (modalidade == 1) {
            custo = CAMINHAO_MEDIO * distancia;
        } else if (modalidade == 2) {
            custo = CAMINHAO_GRANDE * distancia;
        }
        return new ArrayList<>(Arrays.asList(distancia,custo));
    }

    /**
     * Método para cadastrar o transporte na lista do programa.
     *
     * @param cidades Lista com as cidades.
     * @param itens   Lista dos itens.
     */
    public void cadastrarTransporte(@NotNull List<String> cidades, @NotNull List<List<String>> itens) {
        double pesoTotal = 0;
        for (List<String> item : itens) {
            int quantItem = Integer.parseInt(item.get(1));
            double pesoItem = Double.parseDouble(item.get(2));
            pesoTotal += quantItem * pesoItem;
        }
        List<Trecho> listaTrechos = new ArrayList<>();
        String cidadeAnterior = "";
        String cidadeAtual;
        double distanciaTotal = 0;
        List<Double> custos = new ArrayList<>(Arrays.asList(0.0,0.0,0.0,0.0));
        for (int i = 0; i < cidades.size(); i++) {
            if (i == 0) {
                cidadeAnterior = cidades.get(i);
            } else {
                cidadeAtual = cidades.get(i);
                double distancia = consultarTrechosxModalidade(cidadeAnterior, cidadeAtual, 1).get(0);
                distanciaTotal += distancia;
                custos = calcularCustos(custos, cidadeAnterior, cidadeAtual, pesoTotal);
                listaTrechos.add(new Trecho(cidadeAnterior, cidadeAtual, distancia, custos.get(3)));
                cidadeAnterior = cidadeAtual;
            }
        }
        double custoTotal = custos.get(0) + custos.get(1) + custos.get(2);
        List<Double> custoPCaminhao = new ArrayList<>(Arrays.asList(custos.get(0), custos.get(1), custos.get(2)));
        cadastrosTransportes.add(new Transporte(itens, pesoTotal, listaTrechos, distanciaTotal, custoTotal, custoPCaminhao));
    }

    /**
     * Método para calcular os custo total de cada caminhão, e o trecho.
     *
     * @param custos    Lista com as cidades.
     * @param cidadeI   Cidade inicial.
     * @param cidadeF    Lista final.
     * @param pesoTotal  peso total do transporte.
     * @return Lista de double com as posições sendo [0]-Custo do caminhão pequeno, [1]- Custo do caminhão médio,
     * [2]- Custo do caminhão grande e [3] custo total do trecho.
     */
    private List<Double> calcularCustos(@NotNull List<Double> custos, String cidadeI, String cidadeF, double pesoTotal) {
        List<Integer> quantCaminhao = calQuantCaminhao(pesoTotal);
        List<Double> custoC = custoTipoCaminhao(quantCaminhao, cidadeI, cidadeF);
        double custoTrecho = custoC.get(0) + custoC.get(1) + custoC.get(2);
        return new ArrayList<>(Arrays.asList(custos.get(0) + custoC.get(0), custos.get(1) + custoC.get(1), custos.get(2) + custoC.get(2), custoTrecho));
    }

    /**
     * Método para calcular o custo por tipo de caminhão de um trecho.
     *
     * @param quantCaminhao Lista com as quantidades por caminhão.
     * @param cidadeI       Cidade inicial.
     * @param cidadeF       Cidade de destino.
     * @return Retorna uma lista com o custo por tipo de caminhão, sendo [0] - caminhão pequeno,
     * [1] - caminhão médio e [2] - caminhão grande.
     */
    public List<Double> custoTipoCaminhao(@NotNull List<Integer> quantCaminhao, String cidadeI, String cidadeF) {
        double custoP = 0;
        double custoM = 0;
        double custoG = 0;
        if (quantCaminhao.get(0) > 0)
            custoP = quantCaminhao.get(0) * consultarTrechosxModalidade(cidadeI, cidadeF, 0).get(1);
        if (quantCaminhao.get(1) > 0)
            custoM = quantCaminhao.get(1) * consultarTrechosxModalidade(cidadeI, cidadeF, 1).get(1);
        if (quantCaminhao.get(2) > 0)
            custoG = quantCaminhao.get(2) * consultarTrechosxModalidade(cidadeI, cidadeF, 2).get(1);
        return new ArrayList<>(Arrays.asList(custoP, custoM, custoG));
    }

    /**
     * Método para calcular a melhor quantidade de caminhão de cada tipo,
     * para gastar menos.
     *
     * @param pesoTotal peso total dos produtos.
     * @return Retorna uma lista com as quantidades de cada caminhão, sendo [0] - caminhão pequeno,
     * [1] - caminhão médio e [2] - caminhão grande.
     */
    public List<Integer> calQuantCaminhao(double pesoTotal) {
        int quantPequeno = 0;
        int quantMedio = 0;
        int quantGrande = 0;
        while (pesoTotal > 0) {
            if (pesoTotal > 8000) {
                quantGrande += 1;
                pesoTotal -= 10000;
            } else if (pesoTotal > 2000) {
                quantMedio += 1;
                pesoTotal -= 4000;
            } else {
                quantPequeno += 1;
                pesoTotal -= 1000;
            }
        }
        return new ArrayList<>(Arrays.asList(quantPequeno,quantMedio,quantGrande));
    }

    /**
     * Método para calcular dados estatisticos e quantidade total de itens.
     *
     * @param t recebe um Transporte.
     * @return Retorna uma lista com os dados estatísticos pedidos, sendo [0] - custo médio por km,
     * [1] - custo médio por tipo, [2] - quantidade total de itens.
     */
    public List<Double> dadosEstatisticos(@NotNull Transporte t) {
        double custoMKm = t.custoTotal / t.distanciaTotal;
        int quantTipo = 0;
        double totalItens = 0;
        for (int i = 0; i < t.getItens().size(); i++) {
            int quantItem = Integer.parseInt(t.getItens().get(i).get(1));
            if (quantItem > 0)
                quantTipo++;
            totalItens += quantItem;
        }
        double custoMTipo = 0.0;
        if (quantTipo > 0)
            custoMTipo = t.custoTotal / quantTipo;
        return new ArrayList<>(Arrays.asList(custoMKm, custoMTipo, totalItens));
    }

    /**
     * Método para calcular o custo médio de cada tipo de caminhão.
     *
     * @param custoTotal Custo total do trajeto.
     * @param quant      Quantidade por tipo de caminhão;
     * @return Retorna uma lista com os custos médios de cada tipo de caminhões, sendo [0] - caminhão pequeno,
     * [1] - caminhão médio e [2] - caminhão grande.
     */
    public List<Double> custoMedioPTipo(@NotNull List<Double> custoTotal, @NotNull List<Integer> quant) {
        double mediaPequeno = 0.0;
        double mediaMedio = 0.0;
        double mediaGrande = 0.0;
        if (quant.get(0) > 0)
            mediaPequeno = custoTotal.get(0)/quant.get(0);
        if (quant.get(1) > 0)
            mediaMedio = custoTotal.get(1)/quant.get(1);
        if (quant.get(2) > 0)
            mediaGrande = custoTotal.get(2)/quant.get(2);
        return new ArrayList<>(Arrays.asList(mediaPequeno, mediaMedio, mediaGrande));
    }
}
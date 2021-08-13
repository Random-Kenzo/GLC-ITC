//Nome: Guilherme Kenzo Silva Oshiro              NUSP:11314988

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileWriter;

public class glc{
    public static void criaOutput(String saida){                                        //método para criar o arquivo de saida.
        try{
            File myObj = new File (saida);
            if (myObj.createNewFile()){
                System.out.println("arquivo " + saida + " criado");                     //cria o arquivo de saida, caso já exisa ele será reescrito
            } else {
                System.out.println("Ja existe arquivo com nome " + saida + ", ele sera reescrito com os novos resultados.");
            }
        } catch (IOException e) {
            System.out.println("Error.");
            e.printStackTrace();
        }
    }

    public static String testaRule(String [] SubE, String [] SubB, String [] vG, List<List<String>> Rules){
        String Origem = null;                                                       //Origem, funciona de maneira similar à origem na função testaCadeia

        for(int i = 0; i < SubE.length; i++){                                       //loop que itera por todos as variaveis possiveis na esquerda
            for(int j = 0; j < SubB.length; j++){                                   //loop que itera por todas as variaveis possiveis em baixo
                String aux = SubE[i] + " " + SubB[j];                               //aux "junta" os dois lados

                for(int k = 0; k <vG.length; k++){                                  //loop responsavel por iterar pelas variaveis da GLC
                    for(int l = 0; l <Rules.get(k).size(); l++){                    //loop que itera pelas regras de substituição da variavel do loop atual
                        if(aux.equals(Rules.get(k).get(l))){                        //se encontrarmos uma regra de substituição valida adicionamos ela à Origem
                            if (Origem == null) Origem = vG[k];                     
                            else Origem = Origem + "," + vG[k];
                        }
                    }
                }
            }
        }


        return Origem;
    }

    public static int testaCadeia(String [] vG, String [] vT, List<List<String>> Rules, List<String> cadeia){
        if (cadeia.contains("&")){                                                  //Verifica se a cadeia é vazia
            if (Rules.get(0).contains("&")) return 1;                               //caso a glc permite cadeia vazia retorna 1
            else return 0;
        }

        int cs = cadeia.size();
        int vs = vG.length;
        if (cs == 1){                                                               //verifica se a cadeia é formada por um unico elemento
            if (Rules.get(0).contains(cadeia.get(0))) return 1;                     //caso a variavel inicial da glc gera a cadeia retorna 1
            else return 0;
        }

        String [][] matriz  = new String [cs][cs];                                  //cria matriz para realizar o CYK, o tamanho vai ser baseado no tamanho da cadeia
        for(int i = 0; i < cs; i++){                                                //inicializa a diagonal com seus respectivos Variaveis, iterando por cada elemento da cadeia
            String aux = " ";
            for(int j = 0; j < vs; j++){                                            //for para iterar por todos os possiveis variaveis de GLC
                for(int k = 0; k < Rules.get(j).size(); k++) {                      //for interno para iterar pelas regras de substituição de cada variavel
                    if(Rules.get(j).get(k).equals(cadeia.get(i))){                  //Verifica se alguma regra de substituição consegue gerar elemento da cadeia da iteração atual
                        if(aux.equals(" ")) aux = vG[j];                            //Se alguma regra o requesito adiciona para uma string separada por ','
                        else aux = aux + "," + vG[j];                               //No caso de ser o primeiro elemento a ser adicionado a String, começa a String sem ','
                    }
                }
            }
            matriz[i][i] = aux;                                                     //terminando os loops é adicionado essa String para a matriz
        }

        for (int i = 1; i < cs; i++){                                               //Aqui populamos o resto da matriz
            int k = i;                                                              //variavel responsavel por passar pelas linhas, representa a posição atual na matriz durante a iteração
            for (int j = 0; j< (cs - i); j++){                                      //for responsavel por iterar pelas colunas
                String origem = null;                                               //array para armazenar as variaveis que podem gerar a substituição das cadeias
                for (int l = 0; l < i; l++){                                        //loop responsavel pelas comparações dos valores na mesma linha porém colunas anteriores a posição atual da matriz e linhas abaixo da posição atual porém na mesma coluna
                    int Laux = i - l;                                               //variavel para representar a posição a esquerda 
                    int Caux = l + 1;                                               //variavel para representar a posição abaixo respectiva a esquerda (a qual juntas conseguem gerar a subcadeia buscada na posição atual)
                    String aux = matriz[j][k-Laux];                                 //carrega os valores da posição esquerda
                    if(aux != null){                                            
                        String [] SubEsq = aux.split(",");                          //Se for diferente de Null é valida
                        aux = matriz[j+Caux][k];
                        if(aux != null){                                            //carrega a de baixo, se for diferente de null é valida
                            String [] SubBai = aux.split(",");                      
                            aux = testaRule(SubEsq, SubBai, vG, Rules);             //realiza o teste para verifica se há Rules validas para gerar a subcadeia
                            if(aux != null){                                        //caso haja variaveis validas para gerar a subcadeia adicionamos à String Origem
                                if (origem == null) origem = aux;
                                else origem = origem + "," + aux;
                            }
                        }
                    }
                }
                matriz[j][k] = origem;                                              //Ao final dos loops adicionamos a String origem na posição atual da matriz
                    
                k++;                                                                //incrementamos a posição na linha, assim na proxima iteração será usado a posição correta
            }
        }

        if(matriz[0][cs-1] == null) return 0;                                       //Verifica se a posição final da nossa matriz é Null, caso seja Null então a GLC não gera a cadeia atual
        else if(matriz[0][cs-1].equals(vG[0])) return 1;                            //Caso não seja null se verifica se a string retornada é equivalente a variavel inicial (Usamos isso se há apenas uma variavel)


        String [] Resposta = matriz[0][cs-1].split(",");                            //Realiza-se um split entre as variaveis
        for(int i = 0; i< Resposta.length; i++){                                    //para cada variavel se ferifica se ela é a variavel inicial, caso seja retorna 1
            if(Resposta[i].equals(vG[0])) return 1;
        }


        return 0;                                                                   //se chegarmos aqui a GLC não gera a cadeia atual
    }

    public static void main (String [] args){
        int k, q, t, s;                                                             //k = numero de glcs; q =numero de variaveis; t = numero de simbolos; s = numero de regras
        int n;

        try{
            File entrada = new File ("inp-glc.txt");
            File cadeias = new File ("inp-cadeias.txt");
            Scanner si = new Scanner(entrada);                                      //Scanner para leitura de dados do arquivo inp-glc
            Scanner sc = new Scanner(cadeias);                                      //Scanner para leitura de dados do arquivo inp-cadeias
            criaOutput("out-status.txt");                                           //chamda para o metódo de criação de arquivo de saida, ou de sua sobreescrita
            FileWriter wo = new FileWriter("out-status.txt");                       //criação do Writer para escrever o arquivo de saida

            k = si.nextInt();                                                       //armazena o numero de gramaticas a serem testadas

            for(int j = 0; j<k; j++){                                               //loop que itera pelo numero de gramaticas a serem testadas
                q = si.nextInt();                                                   //recebe qj de Gj da iteração atual
                t = si.nextInt();                                                   //recebe tj de Gj da iteração atual
                s = si.nextInt();                                                   //recebe sj de Gj da iteração atual

                String vG[] = new String[q];                                        //cria um vetor vG para armazenar a lista de váriaveris qj da gramtica Gj da iteração atual
                String aux = si.nextLine().trim();                                         //cria uma várivel auxiliar, sua principal função é ler quebra de linhas ou linhas inteiras dos arquivos de entrada
                aux = si.nextLine();
                vG = aux.split(" ");                                                //vG recebe os valores da grámatica Gj, com cada um dos seus indices armazenando um valor diferente para os qj valores de Gj

                String vT[] = new String[t];                                        //cria um vetor vT, com  onde armazenaremos a lista de terminais de Gj
                aux = si.nextLine().trim();
                vT = aux.split(" ");                                                //vT recebe os terminais de Gj, de maneira similar a vG

                List<List<String>> Rules = new ArrayList <List<String>>();          //Lista de lists para armazenar as regras de substituição para cada variavel de Gj
                for(int i = 0; i < q; i++) Rules.add(new ArrayList<String>());      //Inicialização das listas internas, cada lista armazenará todas as regras de substituição de um valor de vG

                for(int h = 0; h < s; h++) {                                        //for que itera por sj para adicionarmos as regras de Gj
                    aux = si.nextLine();                                            //recebe a linha contendo a regra
                    String Raux [] = aux.split(" => ");                             //separa a regra em RAux, Raux[0] = variavel, Raux[1] = variaveis/terminal/& da substituição

                    for(int i = 0; i < q; i++){                                     //loop que itera pelo numero de váriaveis de Gj
                        if(vG[i].equals(Raux[0])){                                  //caso vG[i] for igual a Raux[0], significa que devemos adicionar Raux[1] para a lista[i] (lista que representa as regras de substituição de vG[i])
                            Rules.get(i).add(Raux[1].trim());                              //adicionamos o resultado da substituição para a sua respectiva lista
                            break;                                                  //então saimos do loop
                        }
                    }
                }


                n = sc.nextInt();                                                   //recebe nj do numero de cadeias que deve ser testada por Gj
                aux = sc.nextLine();


                for(int i = 0; i < n; i++){                                         //for que itera por nj
                    aux = sc.nextLine();                                            //recebe a linha com a cadeia a ser avaliada
                    String Caux [] = aux.split(" ");                                //separamos os valores da cadeia em um vetor
                    List <String> cadeia = new ArrayList <String>();                //Lista que armazena a cadeia
                    for(int h = 0; h < Caux.length; h++) cadeia.add(Caux[h]);       //loop que passa os valores de Caux para a cadeia
                    int resultado = testaCadeia(vG, vT, Rules, cadeia);             //chamada do metodo testaCadeia(), ele é responsavel por realizar o algoritimo que testa as cadeias, funciona baseado em um algoritimo CYK
                    wo.write(resultado + " ");                                      //chamada da nossa função writer para escrever no arquivo de saida

                }

                if(j <k-1) wo.write("\n");                                          //escrita de quebra de linha para separar os resultados da próxima GLC 

            }

            si.close();                                                             //Fechamento dos scanners e do writer
            sc.close();
            wo.close();

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
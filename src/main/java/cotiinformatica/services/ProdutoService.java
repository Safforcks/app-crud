package cotiinformatica.services;

import cotiinformatica.entities.Produto;
import cotiinformatica.repositories.ProdutoRepository;

import java.util.Scanner;

public class ProdutoService {

    public void cadastrarProduto() {
        try {

            System.out.println("\nCADASTRO DE PRODUTO:\n");

            var produto = new Produto();
            var scanner = new Scanner(System.in);

            System.out.print("INFORME O NOME DO PRODUTO..: ");
            produto.setNome(scanner.nextLine());

            System.out.print("INFORME O PREÇO............: ");
            produto.setPreco(Double.parseDouble(scanner.nextLine()));

            System.out.print("INFORME A QUANTIDADE.......: ");
            produto.setQuantidade(Integer.parseInt(scanner.nextLine()));

            var produtoRepository = new ProdutoRepository();
            produtoRepository.inserir(produto);

            System.out.println("\nPRODUTO CADASTRADO COM SUCESSO!");

        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }
    }

    public void atualizarProduto() {

        System.out.println("\nATUALIZAÇÃO DE PRODUTO:\n");

        try {

            var produto = new Produto();
            var scanner = new Scanner(System.in);

            System.out.print("INFORME O ID DO PRODUTO..: ");
            produto.setId(Integer.parseInt(scanner.nextLine()));

            System.out.print("INFORME O NOME..........: ");
            produto.setNome(scanner.nextLine());

            System.out.print("INFORME O PREÇO.........: ");
            produto.setPreco(Double.parseDouble(scanner.nextLine()));

            System.out.print("INFORME A QUANTIDADE....: ");
            produto.setQuantidade(Integer.parseInt(scanner.nextLine()));

            var produtoRepository = new ProdutoRepository();

            if(produtoRepository.atualizar(produto)) {
                System.out.println("\nPRODUTO ATUALIZADO COM SUCESSO!");
            } else {
                System.out.println("\nNENHUM PRODUTO ENCONTRADO.");
            }
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }
    }

    public void excluirProduto() {

        System.out.println("\nEXCLUSÃO DE PRODUTO:\n");

        try {

            var scanner = new Scanner(System.in);

            System.out.print("INFORME O ID DO PRODUTO..: ");
            var id = Integer.parseInt(scanner.nextLine());

            var produtoRepository = new ProdutoRepository();

            if(produtoRepository.excluir(id)) {
                System.out.println("\nPRODUTO EXCLUÍDO COM SUCESSO!");
            } else {
                System.out.println("\nNENHUM PRODUTO ENCONTRADO.");
            }
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }
    }

    public void consultarProduto() {

        System.out.println("\nCONSULTA DO PRODUTO:\n");

        try {

            var produtoRepository = new ProdutoRepository();
            var lista = produtoRepository.consultar();

            for (var produto : lista) {
                System.out.println("ID...........: " + produto.getId());
                System.out.println("NOME.........: " + produto.getNome());
                System.out.println("PREÇO........: " + produto.getPreco());
                System.out.println("QTD..........: " + produto.getQuantidade());
                System.out.println(".............." );
            }

        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }
    }
}

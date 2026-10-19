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

    public void atulizarProduto() {

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
            produtoRepository.atualizar(produto);

            System.out.println("\nPRODUTO ATUALIZADO COM SUCESSO!");

        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }
    }

    public void excluirProduto() {

        System.out.println("\nEXCLUSÃO DE PRODUTO:\n");

        try {

            var produto = new Produto();
            var scanner = new Scanner(System.in);

            System.out.print("\nINFORME O ID DO PRODUTO..: ");
            var id = Integer.parseInt(scanner.nextLine());

            var produtoRepository = new ProdutoRepository();
            produtoRepository.excluir(id);

            System.out.println("\nPRODUTO EXCLUÍDO COM SUCESSO!");
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }
    }
}

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
}

package cotiinformatica;

import cotiinformatica.services.ProdutoService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("\nMENU DE OPÇÕES:\n");

        System.out.println("(1) CADASTRAR PRODUTO");
        System.out.println("(2) ATUALIZAR PRODUTO");
        System.out.println("(3) EXCLUIR PRODUTO");
        System.out.println("(4) CONSULTAR PRODUTOS");

        var service = new ProdutoService();
        var scanner = new Scanner(System.in);

        System.out.print("\nINFORME A OPÇÃO DESEJADA: ");
        var opcao = Integer.parseInt(scanner.nextLine());

        switch (opcao) {
            case 1:
                service.cadastrarProduto();
                break;
            default:
                System.out.println("\nOPÇÃO INVÁLIDA!");
                break;
        }

        System.out.print("\nDESEJA FAZER OUTRA OPERAÇÃO? (S/N): ");
        var continuar = scanner.nextLine();

        if (continuar.equalsIgnoreCase("S")) main(args); // recursividade
        else System.out.println("\nFIM DO PROGRAMA!");
    }
}
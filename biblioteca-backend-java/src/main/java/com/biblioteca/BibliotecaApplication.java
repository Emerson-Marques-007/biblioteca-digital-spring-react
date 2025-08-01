package com.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Spring Boot para o Sistema de Gestão de Biblioteca.
 * 
 * Esta aplicação implementa um sistema completo de gestão de biblioteca com:
 * - Gestão de livros (CRUD)
 * - Gestão de usuários (CRUD)
 * - Gestão de empréstimos (CRUD)
 * - Sistema de multas por atraso
 * - API REST para integração com frontend
 * 
 * @author Emerson Marques Cardoso dos Santos
 * @version 1.0
 */
@SpringBootApplication
public class BibliotecaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }
}


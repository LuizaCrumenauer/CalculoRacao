package br.csi.projeto_calculo_racao;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

@OpenAPIDefinition(info = @Info( // Adicione esta anotação
		title = "API Pet Saude",
		version = "1.0",
		description = "API para gerenciamento de controle de qualidade de alimentação de pets",
		contact = @Contact(name = "Luiza Crumenauer", email = "crumenauerluiza@gmail.com")
))

public class ProjetoCalculoRacaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoCalculoRacaoApplication.class, args);

		//System.out.println("Senha criptografada: " + new BCryptPasswordEncoder ().encode("admin"));
	}

}

package br.csi.projeto_calculo_racao.DTO;

import br.csi.projeto_calculo_racao.model.tutor.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record DadosAtualizacaoPerfilTutorDTO(
        String nome,

        @Pattern(
                regexp = "^$|\\(?\\d{2}\\)?\\s?\\d{4,5}-\\d{4}",
                message = "Formato de telefone inválido. Use (XX) XXXXX-XXXX"
        )
        String telefone,
        @Valid Endereco endereco,
        @Pattern(
                regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "CPF deve estar no formato 00000000000 ou 000.000.000-00"
        )
        String novoCpf,
        @Email(message = "Formato de email inválido") String novoEmail,

        String novaSenha,

        // A senha atual é necessária para mudar email OU mudar senha
        String senhaAtual
) {}

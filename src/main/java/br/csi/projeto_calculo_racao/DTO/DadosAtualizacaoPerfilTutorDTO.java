package br.csi.projeto_calculo_racao.DTO;

import br.csi.projeto_calculo_racao.model.tutor.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record DadosAtualizacaoPerfilTutorDTO(
        String nome,
        String telefone,
        @Valid Endereco endereco,
        @Pattern(
                regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "CPF deve estar no formato 00000000000 ou 000.000.000-00"
        )
        String novoCpf,
        @Email(message = "Formato de email inválido") String novoEmail,
        String senhaAtual
) {}

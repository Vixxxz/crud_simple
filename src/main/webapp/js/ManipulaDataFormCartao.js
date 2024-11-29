class ManipulaDataFormCartao {
    constructor(dadoCartaoForm) {
        this.dadoCartaoForm = dadoCartaoForm;
        this.adicionaEventoSubmit();
    }

    adicionaEventoSubmit() {
        const submitButton = document.getElementById('submit-cartao');
        if (submitButton) {
            submitButton.addEventListener('click', (event) => this.enviarFormulario(event));
        } else {
            console.error("Erro: Botão de submissão não encontrado.");
        }
    }

    async enviarFormulario(event) {
        if (event) event.preventDefault(); // Evita refresh da página

        console.log('Tentando enviar formulário...');
        try {
            const camposObrigatorios = ["nCartao", "nomeCartao", "cpfCartao", "bandeira", "codSeguranca"];

            // Valida campos obrigatórios
            this.validaCamposObrigatorios(this.dadoCartaoForm, camposObrigatorios);

            // Monta o JSON para envio
            const cartaoJson = this.montaJson();

            console.log("JSON gerado: ", JSON.stringify(cartaoJson, null, 2));

            // Envia os dados para o servidor via API Fetch
            const resposta = await fetch('http://localhost:8080/crud_v3_war_exploded/controlecartao', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(cartaoJson),
            });

            const dado = await resposta.json();
            console.log('Resposta recebida do servidor:', dado);
            alert('Cartão enviado com sucesso!');
        } catch (error) {
            console.error('Erro ao enviar o formulário:', error.message);

            // Tratamento de erros específicos
            if (error.message.includes("bandeira deve estar cadastrada")) {
                alert("Erro: Bandeira não cadastrada no sistema. Cadastre-a antes de continuar.");
            } else {
                alert("Falha ao enviar o formulário. Verifique os dados e tente novamente.");
            }
        }
    }

    validaCamposObrigatorios(form, camposObrigatorios) {
        if (!form || !form.elements) {
            console.error("Formulário inválido.");
            throw new Error("Formulário não encontrado ou estrutura inválida.");
        }

        const missingFields = camposObrigatorios.filter(field => {
            const element = form.elements[field];
            return !element || !element.value; // Verifica se o campo está vazio
        });

        for (const field of missingFields) {
            const input = form.elements[field];
            if (input && input.classList) {
                input.classList.add("error");
            }
        }

        if (missingFields.length > 0) {
            console.error("Campos obrigatórios ausentes:", missingFields);
            throw new Error(`Campos obrigatórios não preenchidos: ${missingFields.join(', ')}`);
        }
    }

    montaJson() {
        // Captura os valores do formulário, tratando o checkbox
        const numero = this.dadoCartaoForm.elements['nCartao']?.value || null;
        const nomeImpresso = this.dadoCartaoForm.elements['nomeCartao']?.value || null;
        const cpf = this.dadoCartaoForm.elements['cpfCartao']?.value || null;
        const bandeira = this.dadoCartaoForm.elements['bandeira']?.value || null;
        const numSeguranca = this.dadoCartaoForm.elements['codSeguranca']?.value || null;
        const preferencial = this.dadoCartaoForm.elements['preferencial']?.checked || false;

        return {
            Cartao: {
                numero,
                numSeguranca,
                nomeImpresso,
                preferencial, // Sempre retorna `false` se não estiver marcado
            },
            Bandeira: {
                nomeBandeira: bandeira,
            },
            Cliente: {
                cpf,
            },
        };
    }
}
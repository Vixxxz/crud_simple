class ManipulaDataFormBandeira {
    constructor(dadoCartaoForm) {
        this.dadoCartaoForm = dadoCartaoForm;
        this.adicionaEventoSubmit();
    }

    adicionaEventoSubmit() {
        const submitButton = document.getElementById('submit-bandeira');
        if (submitButton) {
            submitButton.addEventListener('click', (event) => this.enviarFormulario(event));
        } else {
            console.error("Erro: Botão de submissão não encontrado.");
        }
    }

    async enviarFormulario(event) {
        if (event) event.preventDefault();

        console.log('Enviando formulários...');

        try {
            let camposObrigatorios = ["criaBandeira"];
            this.validaCamposObrigatorios(this.dadoCartaoForm, camposObrigatorios);

            const bandeiraJson = this.montaJson();

            console.log("JSON gerado: ", JSON.stringify(bandeiraJson, null, 2));

            const resposta = await fetch('http://localhost:8080/crud_v3_war_exploded/controlebandeira', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(bandeiraJson)
            });

            if (!resposta.ok) {
                const errorText = await resposta.text();
                throw new Error(`Erro ${resposta.status}: ${resposta.statusText} - ${errorText}`);
            }

            const dado = await resposta.json();

            console.log('Success: ', dado);
            alert("Bandeira cadastrada com sucesso!");
        } catch (error) {
            console.error('Exceção capturada: ', error.message);
            alert(`Falha ao Cadastrar Bandeira. Erro: ${error.message}`);
        }
    }

    validaCamposObrigatorios(form, camposObrigatorios) {
        if (!form || !form.elements) {
            console.error("Form inválido: ", form);
            throw new Error("Formulário não encontrado ou estrutura inválida.");
        }

        const missingFields = camposObrigatorios.filter(field => {
            const element = form.elements[field];
            return !element || !element.value.trim();
        });

        for (const field of missingFields) {
            const input = form.elements[field];
            console.log(`Verificando campo: ${field}, Valor: ${input ? input.value : 'Não encontrado'}`);
            if (input && input.classList) {
                input.classList.add("error");
                console.log(`Classe 'error' adicionada ao campo: ${field}`);
            }
        }

        if (missingFields.length > 0) {
            throw new Error(`Campos obrigatórios não preenchidos: ${missingFields.join(', ')}`);
        }
    }

    montaJson(){
        return {
            Bandeira: {
                nomeBandeira: this.dadoCartaoForm.elements['criaBandeira']?.value.trim()
            }
        }
    }
}
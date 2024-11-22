class ManipulaDataForm {
    constructor(dadoPessoalForm, enderecoForm) {
        // Guarda as referências dos formulários para dados pessoais e endereço
        this.dadoPessoalForm = dadoPessoalForm;
        this.enderecoForm = enderecoForm;
        // Chama o metodo que adiciona o evento de submit ao botão de envio
        this.adicionaEventoSubmit();
        this.copiaEndereco();
    }

    adicionaEventoSubmit() {
        // Busca o botão de envio pelo ID 'submit-button'
        const submitButton = document.getElementById('submit-button');
        if (submitButton) {
            // Adiciona o evento de clique ao botão para chamar o metodo `enviarFormularios`
            submitButton.addEventListener('click', (event) => this.enviarFormularios(event));
        } else {
            console.error("Erro: Botão de submissão não encontrado.");
        }
    }

    copiaEndereco(){
        const copiaBtn = document.getElementById('sameAsDelivery')
        if(copiaBtn){
            copiaBtn.addEventListener('click', () => {
                this.copiaDadosEndereco();
            });
        }
    }

    async enviarFormularios(event) {
        // Previne o comportamento padrão do botão para evitar refresh da página
        //if (event) event.preventDefault();

        console.log('Enviando formulários...');

        try {
            // valida os dados do formulário de dados pessoais usando os campos especificados
            let camposObrigatorios = ["nome", "genero", "cpf", "tipoTelefone", "telefone", "email", "senha", "dataNascimento"];
            let validacaoDadoPessoal = this.validaCamposObrigatorios(this.dadoPessoalForm, camposObrigatorios);
            // valida os dados do formulário de endereço com campos específicos
            camposObrigatorios = ["ent_numero", "ent_tipoResidencia", "ent_cep", "ent_logradouro", "ent_bairro", "ent_cidade", "ent_estado", "ent_pais", "cob_numero", "cob_tipoResidencia", "cob_cep", "cob_logradouro", "cob_bairro", "cob_cidade", "cob_estado", "cob_pais"];
            let validacaoEndereco = this.validaCamposObrigatorios(this.enderecoForm, camposObrigatorios);

            // Monta um objeto JSON para envio, adicionando o `ranking` e os `enderecosRelacionados`
            const clienteJson = this.montaJson()

            console.log("JSON gerado: ", JSON.stringify(clienteJson, null, 2));

            // Envia os dados para o servidor usando a API Fetch com uma requisição POST
            const resposta = await fetch('http://localhost:8080/crud_v3_war_exploded/controlecliente', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(clienteJson)
            });

            // Recebe a resposta do servidor e a imprime
            const dado = await resposta.json();

            console.log('Success: ', dado);
        } catch (error) {
            console.error('Exceção capturada: ', error);
            alert("Falha ao enviar o formulário. Tente novamente.");
        }
    }

    validaCamposObrigatorios(form, camposObrigatorios) {
        // Verifica se o formulário é válido
        if (!form || !form.elements) {
            console.error("Form inválido: ", form);
        }
    
        // Verifica se há campos faltantes (não preenchidos) e os armazena em `missingFields`
        const missingFields = camposObrigatorios.filter(field => {
            const element = form.elements[field];
            return !element || !element.value;
        });
    
        // Adiciona a classe "error" aos campos obrigatórios que estão vazios
        for (const field of missingFields) {
            const input = form.elements[field];
            console.log(`Verificando campo: ${field}, Valor: ${input ? input.value : 'Não encontrado'}`);
            if (input && input.classList) {
                input.classList.add("error");
                console.log(`Classe 'error' adicionada ao campo: ${field}`);
            }
        }
    }
    

    copiaDadosEndereco() {
        const mesmoEndereco = document.getElementById('sameAsDelivery').checked;

        if (mesmoEndereco) {
            document.getElementById('cob_cep').value = document.getElementById('ent_cep').value;
            document.getElementById('cob_tipoResidencia').value = document.getElementById('ent_tipoResidencia').value;
            document.getElementById('cob_logradouro').value = document.getElementById('ent_logradouro').value;
            document.getElementById('cob_numero').value = document.getElementById('ent_numero').value;
            document.getElementById('cob_complemento').value = document.getElementById('ent_complemento').value;
            document.getElementById('cob_bairro').value = document.getElementById('ent_bairro').value;
            document.getElementById('cob_cidade').value = document.getElementById('ent_cidade').value;
            document.getElementById('cob_estado').value = document.getElementById('ent_estado').value;
            document.getElementById('cob_pais').value = document.getElementById('ent_pais').value;
        } else {
            document.getElementById('cob_cep').value = '';
            document.getElementById('cob_tipoResidencia').value = '';
            document.getElementById('cob_logradouro').value = '';
            document.getElementById('cob_numero').value = '';
            document.getElementById('cob_complemento').value = '';
            document.getElementById('cob_bairro').value = '';
            document.getElementById('cob_cidade').value = '';
            document.getElementById('cob_estado').value = '';
            document.getElementById('cob_pais').value = '';
        }
    }

    montaJson(){
        // Implemente a função para montar o JSON com os dados dos formulários
        // Adicione o `ranking` e os `enderecosRelacionados`
        // Retorne o JSON montado
        const logradouroEntregaCompleto = this.enderecoForm.elements['ent_logradouro']?.value;
        const tpLogradouroEntrega = logradouroEntregaCompleto?.split(' ')[0];
        const logradouroEntrega = logradouroEntregaCompleto?.split(' ').slice(1).join(' ');

        const logradouroCobrancaCompleto = this.enderecoForm.elements['cob_logradouro']?.value;
        const tpLogradouroCobranca = logradouroCobrancaCompleto?.split(' ')[0];
        const logradouroCobranca = logradouroCobrancaCompleto?.split(' ').slice(1).join(' ');

        return {
            Cliente: {
                ranking: 1,
                nome: this.dadoPessoalForm.elements['nome']?.value,
                genero: this.dadoPessoalForm.elements['genero']?.value,
                cpf: this.dadoPessoalForm.elements['cpf']?.value,
                tipoTelefone: this.dadoPessoalForm.elements['tipoTelefone']?.value,
                telefone: this.dadoPessoalForm.elements['telefone']?.value,
                email: this.dadoPessoalForm.elements['email']?.value,
                senha: this.dadoPessoalForm.elements['senha']?.value,
                dataNascimento: this.dadoPessoalForm.elements['dataNascimento']?.value
            },
            ClienteEndereco: [
                {
                    numero: this.enderecoForm.elements['ent_numero']?.value,
                    tipoResidencia: this.enderecoForm.elements['ent_tipoResidencia']?.value,
                    tipoEndereco: "Entrega",
                    observacoes: this.enderecoForm.elements['ent_complemento']?.value,
                    endereco: {
                        cep: this.enderecoForm.elements['ent_cep']?.value,
                        bairro: {
                            bairro: this.enderecoForm.elements['ent_bairro']?.value,
                            cidade: {
                                cidade: this.enderecoForm.elements['ent_cidade']?.value,
                                uf: {
                                    uf: this.enderecoForm.elements['ent_estado']?.value,
                                    pais: {
                                        pais: this.enderecoForm.elements['ent_pais']?.value
                                    }
                                }
                            }
                        },
                        logradouro: {
                            logradouro: logradouroEntrega,
                            tpLogradouro: {
                                tpLogradouro: tpLogradouroEntrega
                            }
                        }
                    }
                },
                {
                    numero: this.enderecoForm.elements['cob_numero']?.value,
                    tipoResidencia: this.enderecoForm.elements['cob_tipoResidencia']?.value,
                    tipoEndereco: "Cobranca",
                    observacoes: this.enderecoForm.elements['cob_complemento']?.value,
                    endereco: {
                        cep: this.enderecoForm.elements['ent_cep']?.value,
                        bairro: {
                            bairro: this.enderecoForm.elements['cob_bairro']?.value,
                            cidade: {
                                cidade: this.enderecoForm.elements['cob_cidade']?.value,
                                uf: {
                                    uf: this.enderecoForm.elements['cob_estado']?.value,
                                    pais: {
                                        pais: this.enderecoForm.elements['cob_pais']?.value
                                    }
                                }
                            }
                        },
                        logradouro: {
                            logradouro: logradouroCobranca,
                            tpLogradouro: {
                                tpLogradouro: tpLogradouroCobranca
                            }
                        }
                    }
                }
            ]
        }
    }
}
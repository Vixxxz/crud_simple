// Obtenha os botões do título dos formulários
const dadoPessoalBtn = document.getElementById("dados-pessoais-title");
const enderecoBtn = document.getElementById("endereco-title"); // Corrigido o id

const dadoPessoal = document.getElementById("form-dados-pessoais");
const endereco = document.getElementById("form-endereco");

// Define o estado inicial ao carregar a página
window.addEventListener('DOMContentLoaded', function() {
    dadoPessoal.style.display = "block"; // Exibe o formulário de dados pessoais
    endereco.style.display = "none";     // Oculta o formulário de endereço
    dadoPessoalBtn.classList.add('active-tab'); // Marca o botão de dados pessoais como ativo
});

// Alterna para Formulário 1
dadoPessoalBtn.addEventListener('click', function() {
    console.log("Switching to Form 1");
    dadoPessoal.style.display = "block";
    endereco.style.display = "none";
    dadoPessoalBtn.classList.add('active-tab');
    enderecoBtn.classList.remove('active-tab');
});

// Alterna para Formulário 2
enderecoBtn.addEventListener('click', function () {
    console.log("Switching to Form 2");
    endereco.style.display = "block";
    dadoPessoal.style.display = "none";
    enderecoBtn.classList.add('active-tab');
    dadoPessoalBtn.classList.remove('active-tab');
});

// Função para enviar os dados dos formulários
function enviarFormularios(event) {
    if (event) event.preventDefault();// Previne o comportamento padrão do envio do formulário
    console.log("Submitting Form Data...");

    // Extrai dados do formulário 1 (dados pessoais)
    const nome = dadoPessoal.elements['nome']?.value;
    const genero = dadoPessoal.elements['genero']?.value;
    const cpf = dadoPessoal.elements['cpf']?.value;
    const tipoTelefone = dadoPessoal.elements['tipoTelefone']?.value;
    const telefone = dadoPessoal.elements['telefone']?.value;
    const email = dadoPessoal.elements['email']?.value;
    const senha = dadoPessoal.elements['senha']?.value;
    const dataNascimento = dadoPessoal.elements['dataNascimento']?.value;

    const requiredDadosPessoaisFields = ["nome", "genero", "cpf", "tipoTelefone", "telefone", "email", "senha", "dataNascimento"];
    const missingDadosPessoaisFields = requiredDadosPessoaisFields.filter(field => !dadoPessoal.elements[field]?.value);

    if (missingDadosPessoaisFields.length) {
        console.error("Error: Form 1 missing fields:", missingDadosPessoaisFields);
        return;
    }

    console.log("Form 1 Data:", {
        nome, genero, cpf, tipoTelefone, telefone, email, senha, dataNascimento
    });

    // Extrai dados do formulário 2 (endereços)
    const numero = endereco.elements['numero']?.value;
    const tipoResidencia = endereco.elements['tipoResidencia']?.value;
    const observacoes = endereco.elements['complemento']?.value;
    const cep = endereco.elements['cep']?.value;
    const logradouroCompleto = endereco.elements['logradouro']?.value;
    const tpLogradouro = logradouroCompleto?.split(' ')[0];
    const logradouro = logradouroCompleto?.split(' ').slice(1).join(' ');
    const bairro = endereco.elements['bairro']?.value;
    const cidade = endereco.elements['cidade']?.value;
    const uf = endereco.elements['estado']?.value;
    const pais = endereco.elements['pais']?.value;

    const checkboxes = endereco.elements['tipoEndereco'];
    const checked = Array.from(checkboxes).filter(cb => cb.checked);
    let tipoEndereco = '';

    if (checked.length === 1) {
        tipoEndereco = checked[0].value;
    } else if (checked.length > 1) {
        tipoEndereco = checked.map(cb => cb.value).join(', ');
    } else {
        tipoEndereco = null;
    }

    const requiredEnderecoFields = ['numero', 'tipoResidencia', 'cep', 'logradouro', 'bairro', 'cidade', 'estado', 'pais'];
    const missingEnderecoFields = requiredEnderecoFields.filter(field => !endereco.elements[field]?.value);
    if (!tipoEndereco) {
        missingEnderecoFields.push('tipoEndereco');
    }

    if (missingEnderecoFields.length > 0) {
        console.error("Error: Form 2 missing fields:", missingEnderecoFields);
        return;
    }

    console.log("Form 2 Data:", {
        numero, tipoResidencia, tipoEndereco, observacoes, cep, logradouroCompleto, tpLogradouro,
        logradouro, bairro, cidade, uf, pais
    });

    // Criar o objeto no formato JSON desejado
    const jsonData = {
        ranking: 1,
        nome: nome,
        genero: genero,
        cpf: cpf,
        tipoTelefone: tipoTelefone,
        telefone: telefone,
        email: email,
        senha: senha,
        dataNascimento: dataNascimento,
        enderecosRelacionados: [
            {
                numero: numero,
                tipoResidencia: tipoResidencia,
                tipoEndereco: tipoEndereco,
                observacoes: observacoes,
                endereco: {
                    cep: cep,
                    bairro: {
                        bairro: bairro,
                        cidade: {
                            cidade: cidade,
                            uf: {
                                uf: uf,
                                pais: {
                                    pais: pais
                                }
                            }
                        }
                    },
                    logradouro: {
                        logradouro: logradouro,
                        tpLogradouro: {
                            tpLogradouro: tpLogradouro
                        }
                    }
                }
            }
        ]
    };

    // Visualiza o JSON no console (para testar)
    console.log("JSON gerado:", JSON.stringify(jsonData, null, 2)); // Exibe o JSON gerado de forma formatada

    // Enviar o JSON via fetch
    fetch('http://localhost:8080/crud_v3_war_exploded/controlecliente', { // Altere para a URL de envio desejada
        method: 'POST', // Define o metodo como post
        headers: {
            'Content-Type': 'application/json' // Define o cabeçalho Content-Type como JSON
        },
        body: JSON.stringify(jsonData) // Converte o objeto jsonData em uma string JSON para enviar no corpo da requisição
    })
        .then(response => response.json()) // Processa a resposta como JSON
        .then(data => {
            console.log('Success:', data); // Exibe a resposta de sucesso no console
        })
        .catch(error => {
            console.error('Error:', error); // Exibe qualquer erro que ocorrer
        });
}

// Associa o evento de envio a ambos os formulários
document.querySelectorAll("form").forEach(form => {
    form.addEventListener("submit", enviarFormularios);
});


// const formUmBtn= document.getElementById("form-1-title");
// const formDoisBtn= document.getElementById("form-1-title");
//
// const form1 = document.getElementById("form-1");
// const form2 = document.getElementById("form-2");
//
// formUmBtn.addEventListener('click', function() {
//     console.log("Switching to Form 1"); // Exibe no console que está alternando para o Formulário 1
//     form1.style.display = "block"; // Mostra o Formulário 1
//     form2.style.display = "none"; // Esconde o Formulário 2
//     formUmBtn.classList.add('active-tab'); // Adiciona a classe 'active-tab' ao botão do Formulário 1
//     formDoisBtn.classList.remove('active-tab');
// })
//
// formDoisBtn.addEventListener('click', function () {
//     console.log("Switching to Form 2"); // Exibe no console que está alternando para o Formulário 2
//     form2.style.display = "block"; // Mostra o Formulário 2
//     form1.style.display = "none"; // Esconde o Formulário 1
//     formDoisBtn.classList.add('active-tab'); // Adiciona a classe 'active-tab' ao botão do Formulário 2
//     formUmBtn.classList.remove('active-tab'); // Remove a classe 'active-tab' do botão do Formulário 1
// });
//
// // Função para enviar os dados dos formulários
// function enviarFormularios() {
//     console.log("Submitting Form Data..."); // Exibe no console que os dados do formulário estão sendo enviados
//     event.preventDefault(); // Previne o comportamento padrão do envio do formulário
//     const form1Data = document.getElementById("form-1"); // Obtém o Formulário 1
//     const form2Data = document.getElementById("form-2"); // Obtém o Formulário 2
//
//     // Verifica se os elementos dos formulários foram encontrados
//     if (!form1Data || !form2Data) {
//         console.error("Error: Form elements not found"); // Exibe um erro se não encontrar os formulários
//         return; // Interrompe a execução da função
//     }
//
//     // Extrai dados do formulário 1 (dados pessoais)
//     const nome = form1Data.elements['nome']?.value;
//     const genero = form1Data.elements['genero']?.value;
//     const cpf = form1Data.elements['cpf']?.value;
//     const tipoTelefone = form1Data.elements['tipoTelefone']?.value;
//     const telefone = form1Data.elements['telefone']?.value;
//     const email = form1Data.elements['email']?.value;
//     const senha = form1Data.elements['senha']?.value;
//     const dataNascimento = form1Data.elements['dataNascimento']?.value;
//
//     // Verifica se todos os campos do formulário 1 estão preenchidos
//     const requiredDadosPessoaisFields = ["nome", "genero", "cpf", "tipoTelefone", "telefone", "email", "senha", "dataNascimento"];
//     const missingDadosPessoaisFields = requiredDadosPessoaisFields.filter(field => !form1Data.elements[field]?.value);
//
//     if (missingDadosPessoaisFields.length) {
//         console.error("Error: Form 1 missing fields: missing dados pessoais fields:", missingDadosPessoaisFields); // Exibe um erro se algum campo estiver vazio
//         return; // Interrompe a execução da função
//     }
//
//     // Exibe os dados do Formulário 1 no console
//     console.log("Form 1 Data: ", {
//         nome: nome,
//         genero: genero,
//         cpf: cpf,
//         tipoTelefone: tipoTelefone,
//         telefone: telefone,
//         email: email,
//         senha: senha,
//         dataNascimento: dataNascimento
//     });
//
//     // Extrai dados do formulário 2 (endereços)
//     const numero = form2Data.elements['numero']?.value;
//     const tipoResidencia = form2Data.elements['tipoResidencia']?.value;
//     const observacoes = form2Data.elements['complemento']?.value;
//     const cep = form2Data.elements['cep']?.value;
//     const logradouroCompleto = form2Data.elements['logradouro']?.value;
//     const tpLogradouro = logradouroCompleto?.split(' ')[0]; // Pega a primeira palavra como tipo de logradouro
//     const logradouro = logradouroCompleto?.split(' ').slice(1).join(' '); // O resto da string é o nome do logradouro
//     const bairro = form2Data.elements['bairro']?.value;
//     const cidade = form2Data.elements['cidade']?.value;
//     const uf = form2Data.elements['estado']?.value;
//     const pais = form2Data.elements['pais']?.value;
//
//     // Verificando tipo de endereço
//     const checkboxes = form2Data.elements['tipoEndereco']; // Obtém os checkboxes para o tipo de endereço
//     const checked = Array.from(checkboxes).filter(cb => cb.checked); // Filtra os checkboxes marcados
//     let tipoEndereco = ''; // Inicializa a variável tipoEndereco
//
//     // Garantindo que tipoEndereco seja atribuído corretamente
//     if (checked.length === 1) {
//         tipoEndereco = checked[0].value; // Um checkbox marcado
//     } else if (checked.length > 1) {
//         tipoEndereco = checked.map(cb => cb.value).join(', '); // Múltiplos checkboxes marcados
//     } else {
//         tipoEndereco = null; // Nenhum checkbox marcado
//     }
//
//     // Campos obrigatórios
//     const requiredEnderecoFields = ['numero', 'tipoResidencia', 'cep', 'logradouro', 'bairro', 'cidade', 'estado', 'pais'];
//     console.log('tipoEndereco:', tipoEndereco); // Exibe o tipo de endereço no console
//
//     // Verificando campos obrigatórios
//     const missingEnderecoFields = requiredEnderecoFields.filter(field => !form2Data.elements[field]?.value); // Verifica se faltam campos obrigatórios
//     if (!tipoEndereco) { // Verifique aqui se tipoEndereco está preenchido
//         missingEnderecoFields.push('tipoEndereco'); // Adiciona tipoEndereco à lista de campos faltantes
//     }
//
//     // Exibe erro se houver campos faltantes
//     if (missingEnderecoFields.length > 0) {
//         console.error("Error: Form 2 data not complete. missing endereco fields:", missingEnderecoFields);
//         return; // Interrompe a execução da função
//     }
//
//     // Exibe os dados do Formulário 2 no console
//     console.log("Form 2 Data: ", {
//         numero: numero,
//         tipoResidencia: tipoResidencia,
//         tipoEndereco: tipoEndereco,
//         observacoes: observacoes,
//         cep: cep,
//         logradouroCompleto: logradouroCompleto,
//         tpLogradouro: tpLogradouro,
//         logradouro: logradouro,
//         bairro: bairro,
//         cidade: cidade,
//         uf: uf,
//         pais: pais
//     });

//     // Criar o objeto no formato JSON desejado
//     const jsonData = {
//         ranking: 1,
//         nome: nome,
//         genero: genero,
//         cpf: cpf,
//         tipoTelefone: tipoTelefone,
//         telefone: telefone,
//         email: email,
//         senha: senha,
//         dataNascimento: dataNascimento,
//         enderecosRelacionados: [
//             {
//                 numero: numero,
//                 tipoResidencia: tipoResidencia,
//                 tipoEndereco: tipoEndereco,
//                 observacoes: observacoes,
//                 endereco: {
//                     cep: cep,
//                     bairro: {
//                         bairro: bairro,
//                         cidade: {
//                             cidade: cidade,
//                             uf: {
//                                 uf: uf,
//                                 pais: {
//                                     pais: pais
//                                 }
//                             }
//                         }
//                     },
//                     logradouro: {
//                         logradouro: logradouro,
//                         tpLogradouro: {
//                             tpLogradouro: tpLogradouro
//                         }
//                     }
//                 }
//             }
//         ]
//     };
//
//     // Visualiza o JSON no console (para testar)
//     console.log("JSON gerado:", JSON.stringify(jsonData, null, 2)); // Exibe o JSON gerado de forma formatada
//
//     // Enviar o JSON via fetch
//     fetch('http://localhost:8080/crud_v3_war_exploded/cliente', { // Altere para a URL de envio desejada
//         method: 'POST', // Define o metodo como post
//         headers: {
//             'Content-Type': 'application/json' // Define o cabeçalho Content-Type como JSON
//         },
//         body: JSON.stringify(jsonData) // Converte o objeto jsonData em uma string JSON para enviar no corpo da requisição
//     })
//         .then(response => response.json()) // Processa a resposta como JSON
//         .then(data => {
//             console.log('Success:', data); // Exibe a resposta de sucesso no console
//         })
//         .catch(error => {
//             console.error('Error:', error); // Exibe qualquer erro que ocorrer
//         });
// }
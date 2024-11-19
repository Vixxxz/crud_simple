// Obtenha os botões do título dos formulários
const dadoPessoalBtn = document.getElementById("dados-pessoais-title");
const enderecoBtn = document.getElementById("endereco-title"); // Corrigido o id

const dadoPessoal = document.getElementById("form-dados-pessoais");
const endereco = document.getElementById("form-endereco");

const manipulaForm = new ManipulaForm(dadoPessoalBtn, enderecoBtn, dadoPessoal, endereco);
const manipulaDataForm = new ManipulaDataForm(dadoPessoal, endereco);


// // Função para enviar os dados dos formulários
// function enviarFormularios(event) {
//     if (event) event.preventDefault();// Previne o comportamento padrão do envio do formulário
//     console.log("Submitting Form Data...");
//
//     // Extrai dados do formulário 1 (dados pessoais)
//     const nome = dadoPessoal.elements['nome']?.value;
//     const genero = dadoPessoal.elements['genero']?.value;
//     const cpf = dadoPessoal.elements['cpf']?.value;
//     const tipoTelefone = dadoPessoal.elements['tipoTelefone']?.value;
//     const telefone = dadoPessoal.elements['telefone']?.value;
//     const email = dadoPessoal.elements['email']?.value;
//     const senha = dadoPessoal.elements['senha']?.value;
//     const dataNascimento = dadoPessoal.elements['dataNascimento']?.value;
//
//     const requiredDadosPessoaisFields = ["nome", "genero", "cpf", "tipoTelefone", "telefone", "email", "senha", "dataNascimento"];
//     const missingDadosPessoaisFields = requiredDadosPessoaisFields.filter(field => !dadoPessoal.elements[field]?.value);
//
//     if (missingDadosPessoaisFields.length) {
//         console.error("Error: Form 1 missing fields:", missingDadosPessoaisFields);
//         return;
//     }
//
//     console.log("Form 1 Data:", {
//         nome, genero, cpf, tipoTelefone, telefone, email, senha, dataNascimento
//     });
//
//     // Extrai dados do formulário 2 (endereços)
//     const numero = endereco.elements['numero']?.value;
//     const tipoResidencia = endereco.elements['tipoResidencia']?.value;
//     const observacoes = endereco.elements['complemento']?.value;
//     const cep = endereco.elements['cep']?.value;
//     const logradouroCompleto = endereco.elements['logradouro']?.value;
//     const tpLogradouro = logradouroCompleto?.split(' ')[0];
//     const logradouro = logradouroCompleto?.split(' ').slice(1).join(' ');
//     const bairro = endereco.elements['bairro']?.value;
//     const cidade = endereco.elements['cidade']?.value;
//     const uf = endereco.elements['estado']?.value;
//     const pais = endereco.elements['pais']?.value;
//
//     const checkboxes = endereco.elements['tipoEndereco'];
//     const checked = Array.from(checkboxes).filter(cb => cb.checked);
//     let tipoEndereco = '';
//
//     if (checked.length === 1) {
//         tipoEndereco = checked[0].value;
//     } else if (checked.length > 1) {
//         tipoEndereco = checked.map(cb => cb.value).join(', ');
//     } else {
//         tipoEndereco = null;
//     }
//
//     const requiredEnderecoFields = ['numero', 'tipoResidencia', 'cep', 'logradouro', 'bairro', 'cidade', 'estado', 'pais'];
//     const missingEnderecoFields = requiredEnderecoFields.filter(field => !endereco.elements[field]?.value);
//     if (!tipoEndereco) {
//         missingEnderecoFields.push('tipoEndereco');
//     }
//
//     if (missingEnderecoFields.length > 0) {
//         console.error("Error: Form 2 missing fields:", missingEnderecoFields);
//         return;
//     }
//
//     console.log("Form 2 Data:", {
//         numero, tipoResidencia, tipoEndereco, observacoes, cep, logradouroCompleto, tpLogradouro,
//         logradouro, bairro, cidade, uf, pais
//     });
//
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
//     fetch('http://localhost:8080/crud_v3_war_exploded/controlecliente', { // Altere para a URL de envio desejada
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
//
// // Associa o evento de envio a ambos os formulários
// document.querySelectorAll("form").forEach(form => {
//     form.addEventListener("submit", enviarFormularios);
// });
// Obtenha os botões do título dos formulários
const dadoPessoalBtn = document.getElementById("dados-pessoais-title");
const enderecoBtn = document.getElementById("endereco-title");


const dadoPessoal = document.getElementById("form-dados-pessoais");
const endereco = document.getElementById("form-endereco");

console.log(dadoPessoal);
console.log(endereco);
console.log(dadoPessoalBtn);
console.log(enderecoBtn);


const manipulaForm = new ManipulaForm(dadoPessoalBtn, enderecoBtn, dadoPessoal, endereco);
const manipulaDataForm = new ManipulaDataForm(dadoPessoal, endereco);
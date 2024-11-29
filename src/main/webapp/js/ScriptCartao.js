// Obtenha os botões do título dos formulários
const cartaoBtn = document.getElementById("cartao-title");
const bandeiraBtn = document.getElementById("bandeira-title");


const cartao = document.getElementById("form-cartao");
const bandeira = document.getElementById("form-bandeira");

console.log(cartaoBtn);
console.log(bandeiraBtn);
console.log(cartao);
console.log(bandeira);


const manipulaFormCartao = new ManipulaCartao(cartaoBtn, bandeiraBtn, cartao, bandeira);
const manipulaDataFormCartao = new ManipulaDataFormCartao(cartao);
const manipulaDataFormBandeira = new ManipulaDataFormBandeira(bandeira);
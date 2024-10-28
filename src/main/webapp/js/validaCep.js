function limpa_formulario_cep() {
    console.log("Clearing form values...");
    //Limpa valores do formulário de cep.
    console.log("Clearing logradouro");
    document.getElementById('logradouro').value=("");
    console.log("Clearing bairro");
    document.getElementById('bairro').value=("");
    console.log("Clearing cidade");
    document.getElementById('cidade').value=("");
    console.log("Clearing estado");
    document.getElementById('estado').value=("");
    console.log("Clearing pais");
    document.getElementById('pais').value=("");
}

function meu_callback(respostaCep) {
    console.log("meu_callback called with:", respostaCep);
    if (!("erro" in respostaCep)) {
        console.log("CEP found:", respostaCep);
        //Atualiza os campos com os valores.
        console.log("Setting logradouro to", respostaCep.logradouro);
        document.getElementById('logradouro').value=(respostaCep.logradouro);
        console.log("Setting bairro to", respostaCep.bairro);
        document.getElementById('bairro').value=(respostaCep.bairro);
        console.log("Setting cidade to", respostaCep.localidade);
        document.getElementById('cidade').value=(respostaCep.localidade);
        console.log("Setting estado to", respostaCep.uf);
        document.getElementById('estado').value=(respostaCep.uf);
        console.log("Setting pais to Brasil");
        document.getElementById('pais').value="Brasil";
    } //end if.
    else {
        console.log("CEP not found");
        //CEP não Encontrado.
        limpa_formulario_cep();
        console.log("Showing alert CEP não encontrado.");
        alert("CEP não encontrado.");
    }
}

function pesquisacep(cep) {
    console.log("pesquisacep called with:", cep);
    //Nova variável "cep" somente com dígitos.
    var cepFormatado = cep.replace(/\D/g, '');

    //Verifica se campo cep possui valor informado.
    if (cepFormatado !== "") {

        console.log("CEP is not empty");
        //Expressão regular para validar o CEP.
        var validacep = /^[0-9]{8}$/;

        //Valida o formato do CEP.
        if(validacep.test(cepFormatado)) {

            console.log("Valid CEP format");
            //Preenche os campos com "..." enquanto consulta webservice.
            console.log("Setting logradouro to '...'");
            document.getElementById('logradouro').value="...";
            console.log("Setting bairro to '...'");
            document.getElementById('bairro').value="...";
            console.log("Setting cidade to '...'");
            document.getElementById('cidade').value="...";
            console.log("Setting estado to '...'");
            document.getElementById('estado').value="...";
            console.log("Setting pais to '...'");
            document.getElementById('pais').value="...";
            console.log("Fields set to '...'");

            //Cria um elemento javascript.
            var script = document.createElement('script');

            //Sincroniza com o callback.
            script.src = 'https://viacep.com.br/ws/'+ cepFormatado + '/json/?callback=meu_callback';

            //Insere script no documento e carrega o conteúdo.
            document.body.appendChild(script);

        } //end if.
        else {
            console.log("Invalid CEP format");
            //cep é inválido.
            limpa_formulario_cep();
            console.log("Showing alert Formato de CEP inválido");
            alert("Formato de CEP inválido");
        }
    } //end if.
    else {
        console.log("No CEP value");
        //cep sem valor, limpa formulário.
        limpa_formulario_cep();
    }
};

document.getElementById('cep').addEventListener('focusout',()=>{
    console.log("CEP changed, searching...");
    pesquisacep(cep.value);
})
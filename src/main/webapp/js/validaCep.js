let prefixo = ''; // Variável global para armazenar o prefixo atual

function limpa_formulario_cep() {
    console.log("Clearing form values for: " + prefixo);
    // Limpa valores do formulário de cep.
    document.getElementById(`${prefixo}_logradouro`).value = "";
    document.getElementById(`${prefixo}_bairro`).value = "";
    document.getElementById(`${prefixo}_cidade`).value = "";
    document.getElementById(`${prefixo}_estado`).value = "";
    document.getElementById(`${prefixo}_pais`).value = "";
}

function meu_callback(respostaCep) {
    console.log("meu_callback called with:", respostaCep);
    if (!("erro" in respostaCep)) {
        console.log("CEP found:", respostaCep);
        // Atualiza os campos com os valores.
        if (prefixo) {
            document.getElementById(`${prefixo}_logradouro`).value = respostaCep.logradouro;
            document.getElementById(`${prefixo}_bairro`).value = respostaCep.bairro;
            document.getElementById(`${prefixo}_cidade`).value = respostaCep.localidade;
            document.getElementById(`${prefixo}_estado`).value = respostaCep.uf;
            document.getElementById(`${prefixo}_pais`).value = "Brasil";
        }
    } else {
        console.log("CEP not found");
        limpa_formulario_cep();
        alert("CEP não encontrado.");
    }
}

function pesquisacep(cep, novoPrefixo) {
    console.log("pesquisacep called with:", cep);
    prefixo = novoPrefixo; // Atualiza a variável global com o novo prefixo
    const cepFormatado = cep.replace(/\D/g, '');

    if (cepFormatado !== "") {
        console.log("CEP is not empty");
        const validacep = /^[0-9]{8}$/;

        if (validacep.test(cepFormatado)) {
            console.log("Valid CEP format");
            document.getElementById(`${prefixo}_logradouro`).value = "...";
            document.getElementById(`${prefixo}_bairro`).value = "...";
            document.getElementById(`${prefixo}_cidade`).value = "...";
            document.getElementById(`${prefixo}_estado`).value = "...";
            document.getElementById(`${prefixo}_pais`).value = "...";

            const script = document.createElement('script');
            script.src = `https://viacep.com.br/ws/${cepFormatado}/json/?callback=meu_callback`;
            document.body.appendChild(script);
        } else {
            console.log("Invalid CEP format");
            limpa_formulario_cep();
            alert("Formato de CEP inválido");
        }
    } else {
        console.log("No CEP value");
        limpa_formulario_cep();
    }
}

document.getElementById('cob_cep').addEventListener('focusout', () => {
    console.log("CEP changed, searching...");
    pesquisacep(document.getElementById('cob_cep').value, 'cob');
});

document.getElementById('ent_cep').addEventListener('focusout', () => {
    console.log("CEP changed, searching...");
    pesquisacep(document.getElementById('ent_cep').value, 'ent');
});
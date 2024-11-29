document.getElementById('filtroForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const queryParams = criarQueryParams(new FormData(e.target));

    try {
        const url = `http://localhost:8080/crud_v3_war_exploded/controlecliente?${queryParams}`;
        console.log("URL gerada:", url);

        const resposta = await fetch(url);
        const clientes = await resposta.json();

        if (Array.isArray(clientes)) {  // Verifica se a resposta é um array
            renderTabela(clientes);
        } else {
            console.error('Erro: A resposta não é um array.', clientes);
            alert('Erro ao buscar clientes. A resposta da API não é válida.');
        }
    } catch (error) {
        console.error('Erro ao buscar clientes:', error);
        alert('Erro ao buscar clientes. Por favor, tente novamente.');
    }
});

function renderTabela(clientes) {
    const tbody = document.querySelector('#table-clientes tbody');
    tbody.innerHTML = ''; // Limpa os resultados anteriores

    // Verifica se 'clientes' é um array antes de iterar
    if (!Array.isArray(clientes)) {
        console.error('Erro: clientes não é um array:', clientes);
        return;  // Se não for um array, retorna sem tentar renderizar
    }

    clientes.forEach(cliente => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${cliente.id}</td>
            <td>${cliente.nome}</td>
            <td>${cliente.cpf}</td>
            <td>${cliente.dataNascimento}</td>
            <td>${cliente.telefone}</td>
            <td>${cliente.email}</td>
            <td>
                <button class="btn-endereco" data-id="${cliente.id}" data-endereco='${JSON.stringify(cliente.endereco || {})}'>Endereço</button>
                <button class="btn-cartao" data-id="${cliente.id}" data-cartao='${JSON.stringify(cliente.cartao || {})}'>Cartão</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    adicionarEventosModais();
}

function adicionarEventosModais() {
    document.querySelectorAll('.btn-endereco').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const endereco = JSON.parse(e.target.getAttribute('data-endereco'));
            exibirModal('modalEndereco', endereco, formatarEndereco);
        });
    });

    document.querySelectorAll('.btn-cartao').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const cartao = JSON.parse(e.target.getAttribute('data-cartao'));
            exibirModal('modalCartao', cartao, formatarCartao);
        });
    });
}

function exibirModal(modalId, data, formatarConteudo) {
    const modal = document.getElementById(modalId);
    const infoDiv = modal.querySelector('.modal-content');
    infoDiv.innerHTML = formatarConteudo(data);
    modal.style.display = 'flex';

    // Adiciona evento para fechar o modal
    modal.querySelector('.close-modal').addEventListener('click', () => {
        modal.style.display = 'none';
    });
}

function formatarEndereco(endereco) {
    if (!endereco) return 'Endereço não disponível.';
    return `
        Rua: ${endereco.tipoLogradouro || ''} ${endereco.logradouro || ''}<br>
        Número: ${endereco.numero || ''}<br>
        Bairro: ${endereco.bairro || ''}<br>
        Cidade: ${endereco.cidade || ''}<br>
        Estado: ${endereco.estado || ''}
    `;
}

function formatarCartao(cartao) {
    if (!cartao) return 'Cartão não disponível.';
    return `
        Número do Cartão: ${cartao.numero || ''}<br>
        Bandeira: ${cartao.bandeira || ''}<br>
        Data de Vencimento: ${cartao.dataVencimento || ''}
    `;
}
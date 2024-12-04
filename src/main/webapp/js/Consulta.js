// URL base da API
const BASE_URL = "http://localhost:8080/crud_v3_war_exploded";

// Função principal para realizar a consulta de clientes
async function realizarConsultaClientes() {
    const filtroForm = document.getElementById('filtroForm');
    const queryParams = criarQueryParams(new FormData(filtroForm));
    const url = `${BASE_URL}/controlecliente?${queryParams}`;

    try {
        const respostaJson = await fetchAPI(url);
        if (respostaJson?.dados?.length) {
            renderTabela(respostaJson.dados);
        } else {
            mostrarErro('Nenhum cliente encontrado ou resposta inválida.');
        }
    } catch (error) {
        mostrarErro('Erro ao buscar clientes.', error);
    }
}

// Função para buscar o endereço de um cliente pelo ID
async function buscarEnderecoPorId(clienteId) {
    const url = `${BASE_URL}/controleclienteendereco?idCliente=${clienteId}`;
    const resposta = await fetchAPI(url, 'Erro ao buscar endereço');
    console.log('Endereços retornados pela API:', resposta);
    return resposta;
}

// Função genérica para realizar fetch e tratar erros
async function fetchAPI(url, mensagemErro = 'Erro na requisição') {
    try {
        const resposta = await fetch(url);
        if (!resposta.ok) throw new Error(`${mensagemErro}: ${resposta.statusText}`);
        return await resposta.json();
    } catch (error) {
        console.error(mensagemErro, error);
        alert(mensagemErro);
        return null;
    }
}

// Função para exibir os dados no modal
function exibirModal(modalId, conteudo) {
    const modal = document.getElementById(modalId);
    modal.querySelector('.modal-content').innerHTML = `
        <div class="modal-header">
            <h3>${modalId === 'modalEndereco' ? 'Endereço' : 'Cartão'}</h3>
            <button class="close-modal btn btn-danger btn-sm">Fechar</button>
        </div>
        <div class="modal-body">${conteudo}</div>
    `;
    modal.style.display = 'flex';

    modal.querySelector('.close-modal').addEventListener('click', () => {
        modal.style.display = 'none';
    });
}

// Função para renderizar a tabela de clientes
function renderTabela(clientes) {
    const tbody = document.querySelector('#table-clientes tbody');
    tbody.innerHTML = '';

    clientes.forEach(cliente => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${cliente.id || ''}</td>
            <td>${cliente.nome || ''}</td>
            <td>${cliente.cpf || ''}</td>
            <td>${cliente.dataNascimento || ''}</td>
            <td>${cliente.telefone || ''}</td>
            <td>${cliente.email || ''}</td>
            <td>
                <button class="btn-endereco btn btn-sm btn-primary" data-id="${cliente.id}">Endereço</button>
                <button class="btn-cartao btn btn-sm btn-secondary" data-id="${cliente.id}">Cartão</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    adicionarEventosModais();
}

// Função para adicionar eventos aos botões de modais
function adicionarEventosModais() {
    document.querySelectorAll('.btn-endereco').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const clienteId = e.target.getAttribute('data-id');
            try {
                const enderecos = await buscarEnderecoPorId(clienteId);
                const conteudo = formatarEndereco(enderecos);
                exibirModal('modalEndereco', conteudo);
            } catch (error) {
                exibirModal('modalEndereco', 'Erro ao carregar endereço.');
                console.error('Erro ao exibir endereço:', error);
            }
        });
    });

    document.querySelectorAll('.btn-cartao').forEach(btn => {
        btn.addEventListener('click', (e) => {
            exibirModal('modalCartao', 'Funcionalidade não implementada.');
        });
    });
}

// Função para formatar os dados do endereço
function formatarEndereco(dados) {
    if (!dados || !dados.dados || dados.dados.length === 0) return 'Nenhum endereço encontrado.';

    return dados.dados.map(item => {
        const endereco = item.endereco || {};
        const logradouro = endereco.logradouro || {};
        const bairro = endereco.bairro || {};
        const cidade = bairro.cidade || {};
        const uf = cidade.uf || {};
        const pais = uf.pais || {};

        return `
            <div style="margin-bottom: 20px;">
                <p><strong>Tipo de Endereço:</strong> ${item.tipoEndereco || 'N/A'}</p>
                <p><strong>Tipo de Residência:</strong> ${item.tipoResidencia || 'N/A'}</p>
                <p><strong>Rua:</strong> ${logradouro.tpLogradouro?.tpLogradouro || 'N/A'} ${logradouro.logradouro || 'N/A'}</p>
                <p><strong>Número:</strong> ${item.numero || 'N/A'}</p>
                <p><strong>Bairro:</strong> ${bairro.bairro || 'N/A'}</p>
                <p><strong>Cidade:</strong> ${cidade.cidade || 'N/A'}</p>
                <p><strong>Estado:</strong> ${uf.uf || 'N/A'}</p>
                <p><strong>País:</strong> ${pais.pais || 'N/A'}</p>
                <p><strong>CEP:</strong> ${endereco.cep || 'N/A'}</p>
                <p><strong>Observações:</strong> ${item.observacoes || 'N/A'}</p>
            </div>
        `;
    }).join('');
}

// Função para criar os parâmetros de consulta
function criarQueryParams(formData) {
    const params = new URLSearchParams();
    for (const [key, value] of formData.entries()) {
        if (value.trim() !== "") params.append(key, value.trim());
    }
    return params.toString();
}

// Função para exibir mensagens de erro
function mostrarErro(mensagem, error = null) {
    console.error(mensagem, error);
    alert(mensagem);
}

// Inicialização ao carregar a página
document.addEventListener('DOMContentLoaded', () => realizarConsultaClientes());

// Evento para o envio do formulário de filtro
document.getElementById('filtroForm').addEventListener('submit', (e) => {
    e.preventDefault();
    realizarConsultaClientes();
});
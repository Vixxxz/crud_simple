class ManipulaForm {
    constructor(dadoPessoalBtn, enderecoBtn, dadoPessoalForm, enderecoForm) {
        this.dadoPessoalBtn = dadoPessoalBtn;
        this.enderecoBtn = enderecoBtn;
        this.dadoPessoalForm = dadoPessoalForm;
        this.enderecoForm = enderecoForm;
        this.init();
    }

    init() {
        window.addEventListener('DOMContentLoaded', () => {
            this.showForm(this.dadoPessoalForm, this.enderecoForm, this.dadoPessoalBtn, this.enderecoBtn);
        });

        this.dadoPessoalBtn.addEventListener('click', () => {
            this.showForm(this.dadoPessoalForm, this.enderecoForm, this.dadoPessoalBtn, this.enderecoBtn);
        });

        this.enderecoBtn.addEventListener('click', () => {
            this.showForm(this.enderecoForm, this.dadoPessoalForm, this.enderecoBtn, this.dadoPessoalBtn);
        });
    }

    showForm(showForm, hideForm, activeBtn, inactiveBtn) {
        showForm.style.display = "block";
        hideForm.style.display = "none";
        activeBtn.classList.add('active-tab');
        inactiveBtn.classList.remove('active-tab');
    }
}

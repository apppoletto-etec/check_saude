<div align="center">
  <img src="app/src/main/res/drawable/logo_medicina.png" width="120" alt="Logo do Check Saúde">
  <h1>Check Saúde</h1>
  <p>Acompanhamento de exames ocupacionais para uma rotina de trabalho mais saudável e segura.</p>

  ![Android](https://img.shields.io/badge/Android-24%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
  ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
</div>

## Sobre o projeto

O **Check Saúde** é um aplicativo Android de apoio à medicina do trabalho. Ele organiza funcionários e registros de exames ocupacionais, facilitando a consulta das informações necessárias para o acompanhamento da saúde nas empresas.

Este aplicativo integra a **SST Digital: Tecnologia a Serviço da Segurança**, um ecossistema acadêmico que demonstra como soluções digitais podem organizar exames ocupacionais, apoiar o acompanhamento de prazos e reduzir processos manuais. O projeto tem finalidade educacional e demonstrativa.

## Funcionalidades

- Autenticação e cadastro de usuários;
- Cadastro e gerenciamento de funcionários;
- Registro de exames ocupacionais;
- Listagem e consulta de exames;
- Armazenamento e sincronização com Firebase.

## Tecnologias

- Android SDK com Java e Kotlin;
- Material Design e View Binding;
- Firebase Authentication, Firestore, Realtime Database e Storage;
- Picasso para carregamento de imagens;
- Gradle Kotlin DSL.

## Como executar

1. Clone este repositório.
2. Abra o projeto no Android Studio.
3. Configure um projeto Firebase e adicione `app/google-services.json`.
4. Sincronize as dependências do Gradle.
5. Execute em um dispositivo ou emulador com Android 7.0 (API 24) ou superior.

```bash
git clone https://github.com/apppoletto-etec/check_saude.git
```

## Estrutura

```text
app/src/main/
├── java/com/lima/medicinatrabalho/
│   ├── adapter/   # Listagens e componentes de interface
│   ├── bd/        # Integração com Firebase
│   ├── model/     # Funcionários e exames
│   └── view/      # Telas e fluxos do aplicativo
└── res/           # Layouts, imagens, menus e temas
```

## Objetivo

Simplificar o controle de exames ocupacionais e tornar as informações de saúde do trabalho mais organizadas e acessíveis.

---

<div align="center">Parte do ecossistema <strong>SST Digital: Tecnologia a Serviço da Segurança</strong>.</div>

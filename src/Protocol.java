package P2PFileShare_CC.src;

public class Protocol {

    enum Type {
        REQUEST,
        RESPONSE
    }

    enum Query {
        GET,
        REGISTER,
        UPDATE
    }

    private static String Value;
    private Query query;
    private Type type;
    private String content;
    private int ID;

}

// GETTERS, SETTERS,
// CONEXAO TCP
// PEGAR NA CONEXAO TCP E ALTERAR PARA CRIAR UM OBJETO QUERY DEPOIS DE DAR PARSE
// AO INPUT
// FSNode: GET file1 ---> FSTracker, parse da string, transformar em objeto
// query.
// Alternativa: en vez de enviar GET file1, criar primeiro objeto e mandar
// objeto pelo socket TCP.
// Feedback da conexao e das mensagens.
// FSTracker: [FSNodes];
// FSNode tem de ter as informaçoes de blocos de fciheiros HashMap<(nome do
// ficheiro),([blocos desse ficheiro])>
// desenvolver as açoes em cada componente para as diferentes queries: GET :
// getQuery(file) -> lista de fsnodes e os blocos em cada um
// REGISTER : registerNode(FSNode) -> feeback -> atualizar a base de dados do
// fstracker
// UPDATE :
//
package com.lima.medicinatrabalho.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lima.medicinatrabalho.R;
import com.lima.medicinatrabalho.model.EntregaExames;
import com.lima.medicinatrabalho.model.Funcionarios;
import com.lima.medicinatrabalho.view.ListagemExameActivity;
import com.lima.medicinatrabalho.view.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterListagem extends RecyclerView.Adapter<AdapterListagem.MyViewHolder> {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    private static final String TAG = "AdapterListagem";  // Tag para logs

    private List<EntregaExames> listagem;
    private Context context;

    Funcionarios func = new Funcionarios();

    public AdapterListagem(List<EntregaExames> listagem, Context context) {
        this.listagem = listagem;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_listagem, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        EntregaExames entregaExame = listagem.get(position);

        // Configurando os valores nos campos de texto
        holder.funcionario.setText(entregaExame.getNomeFuncExame());
        holder.tipoExame.setText(entregaExame.getTipoExame());
        holder.status.setText(entregaExame.getStatusExame());
        holder.dataVencimento.setText(entregaExame.getDataProximaEntrega());
        holder.dataEntrega.setText(entregaExame.getDataEntrega());
        holder.funcao.setText(entregaExame.getFuncionario().getFuncao());


        // Verificar a data de entrega para alterar a visibilidade do botão e solicitar permissão para enviar notificação
        verificarDataProximaEntrega(entregaExame.getDataProximaEntrega(), holder.botaoAtualizarEntrega, entregaExame.getNomeFuncExame(), entregaExame.getTipoExame(), context);

        // Adicionar ação ao botão
        holder.botaoAtualizarEntrega.setOnClickListener(v -> navegarParaEntregaEpiActivity(entregaExame));
    }

    @Override
    public int getItemCount() {
        return listagem.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView funcionario;
        TextView tipoExame;
        TextView status;
        TextView funcao;
        TextView dataEntrega;
        TextView dataVencimento;
        Button botaoAtualizarEntrega;

        String funcionarioid;

        public MyViewHolder(View itemView) {
            super(itemView);


            funcionario = itemView.findViewById(R.id.textFuncionario);
            tipoExame = itemView.findViewById(R.id.textTipoExame);
            status = itemView.findViewById(R.id.textStatus);
            dataEntrega = itemView.findViewById(R.id.textDataEntrega);
            dataVencimento = itemView.findViewById(R.id.textVencimento);
            botaoAtualizarEntrega = itemView.findViewById(R.id.btnAtualizarEntrega);
            funcao = itemView.findViewById(R.id.textFuncao);
        }
    }

    private void verificarDataProximaEntrega(String dataProximaEntrega, Button botaoAtualizarEntrega, String nomeFuncionario, String tipoExame, Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Verifica se a string de data não é nula ou vazia
        if (dataProximaEntrega == null || dataProximaEntrega.trim().isEmpty()) {
            Log.d(TAG, "Data de próxima entrega não preenchida. Ocultando botão.");
            botaoAtualizarEntrega.setVisibility(View.INVISIBLE);
            return; // Sai do método se a data não estiver preenchida
        }

        try {
            Log.d(TAG, "Verificando data de próxima entrega: " + dataProximaEntrega);
            Date dataEntrega = sdf.parse(dataProximaEntrega);
            Date dataAtual = new Date(); // Data atual

            if (dataEntrega != null && !dataEntrega.after(dataAtual)) {
                Log.d(TAG, "Data de entrega é igual ou anterior à data atual. Exibindo botão.");
                botaoAtualizarEntrega.setVisibility(View.VISIBLE);
                // Verificar e solicitar permissão para enviar notificações
                verificarPermissaoNotificacao(context, nomeFuncionario, tipoExame);
            } else {
                Log.d(TAG, "Data de entrega é posterior à data atual. Ocultando botão.");
                botaoAtualizarEntrega.setVisibility(View.INVISIBLE);
            }
        } catch (ParseException e) {
            Log.e(TAG, "Erro ao analisar a data de próxima entrega", e);
            botaoAtualizarEntrega.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao verificar data de próxima entrega", e);
            botaoAtualizarEntrega.setVisibility(View.INVISIBLE);
        }
    }


    private void verificarPermissaoNotificacao(Context context, String nomeFuncionario, String tipoExame) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissão para notificações não concedida. Solicitando permissão.");

                // Verifica se o contexto é uma atividade antes de solicitar permissões
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
                } else {
                    Log.e(TAG, "Contexto não é uma atividade, não é possível solicitar permissões.");
                }
            } else {
                Log.d(TAG, "Permissão para notificações concedida. Enviando notificação.");
                enviarNotificacao(context, "Exame vencido", "O exame " + tipoExame + " de " + nomeFuncionario + " já venceu!");
            }
        } else {
            Log.d(TAG, "Versão do Android inferior ao 13. Enviando notificação sem solicitar permissão.");
            enviarNotificacao(context, "Exame vencido", "O exame " + tipoExame + " de " + nomeFuncionario + " já venceu!");
        }
    }


    private void enviarNotificacao(Context context, String titulo, String mensagem) {
        String canalId = "Exame_Vencido_Canal";
        String canalNome = "Notificações de Exames Vencidos";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(canalId, canalNome, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder notificacaoBuilder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.logo_medicina)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(1, notificacaoBuilder.build());
    }

    private void navegarParaEntregaEpiActivity(@NonNull EntregaExames entregaExames) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("id", entregaExames.getId());
        intent.putExtra("idFuncionario", entregaExames.getFuncionario().getId());
        intent.putExtra("nome", entregaExames.getNomeFuncExame());
        intent.putExtra("tipoExame", entregaExames.getTipoExame());
        intent.putExtra("status", entregaExames.getStatusExame());
        intent.putExtra("dataEntregaIntent", entregaExames.getDataEntrega());
        intent.putExtra("proximaEntregaIntent", entregaExames.getDataProximaEntrega());

        // Verifique se o contexto é uma Activity antes de iniciar
        if (context instanceof Activity) {
            context.startActivity(intent);
        } else {
            // Se context não for uma Activity, você pode precisar ajustar a lógica
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}


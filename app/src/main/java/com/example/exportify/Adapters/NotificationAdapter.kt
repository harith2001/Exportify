import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.exportify.R
import com.example.exportify.models.AdminNotificationModel

// Modify the NotificationAdapter to handle PDFs
class NotificationAdapter(private val nList: List<AdminNotificationModel>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var listener: OnNotificationClickListener? = null

    interface OnNotificationClickListener {
        fun onNotificationClick(position: Int)
    }

    fun setOnNotificationClickListener(listener: OnNotificationClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topic: TextView = itemView.findViewById(R.id.tvTopic)
        val type: TextView = itemView.findViewById(R.id.tvnotitype)
        val description: TextView = itemView.findViewById(R.id.tvnotiDes)
        val webView: WebView = itemView.findViewById(R.id.pdf)

        init {
            itemView.setOnClickListener {
                listener?.onNotificationClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return nList.size
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = nList[position]

        holder.topic.text = notification.topic
        holder.type.text = notification.type
        holder.description.text = notification.description

        holder.webView.settings.javaScriptEnabled = true
        holder.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.endsWith(".pdf")) {
                    // If the URL ends with .pdf, download the PDF
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view?.context?.startActivity(intent)
                    return true
                }
                return false
            }
        }

        if (notification.pdfUrl!!.isNotEmpty()) {
            // If there is a PDF URL, load and display the PDF in the WebView
            holder.webView.visibility = View.VISIBLE
            holder.webView.loadUrl("http://drive.google.com/viewer?url=${notification.pdfUrl}")
        } else {
            // Hide the WebView if no PDF URL is provided
            holder.webView.visibility = View.GONE
        }
    }
}

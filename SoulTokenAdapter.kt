// Simplified adapter
class SoulTokenAdapter : ListAdapter<SoulToken, SoulTokenAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_token_image)
        val tvId: TextView = view.findViewById(R.id.tv_token_id)
    }

    // ... onCreateViewHolder, onBindViewHolder using Coil to load IPFS image
}

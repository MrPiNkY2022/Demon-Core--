class EventListenerWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        EventSubscriber.subscribeToEvents()
        EventSubscriber.karmaUpdateFlow.collect { (tokenId, newKarma) ->
            sendNotification("Karma Update", "Token #$tokenId now has $newKarma karma! 🔱")
        }
        return Result.success()
    }

    private fun sendNotification(title: String, text: String) {
        val notification = NotificationCompat.Builder(applicationContext, "criticality_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(Random().nextInt(), notification)
    }
}

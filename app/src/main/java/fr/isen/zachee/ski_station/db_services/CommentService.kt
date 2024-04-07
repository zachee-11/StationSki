package fr.isen.zachee.ski_station.db_services

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.dataclass.Comments


class CommentService(private val database: FirebaseDatabase) {
    fun addComment(comment: Comments, onSuccess: () -> Unit, onFailure: (error: String) -> Unit) {
        val commentRef = database.reference.child("comments").push()
        commentRef.setValue(comment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Erreur inconnue") }
    }
    fun getCommentsForLift(liftId: String, onSuccess: (List<Comments>) -> Unit, onFailure: (error: String) -> Unit) {
        database.reference.child("comments").orderByChild("liftID").equalTo(liftId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments = dataSnapshot.children.mapNotNull { it.getValue(Comments::class.java) }
                    onSuccess(comments)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onFailure(databaseError.message)
                }
            })
    }

       fun deleteComment(commentId: String, onSuccess: () -> Unit, onFailure: (error: String) -> Unit) {
        database.reference.child("comments").child(commentId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Erreur inconnue") }
    }
}
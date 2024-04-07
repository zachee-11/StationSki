package fr.isen.zachee.ski_station.db_services

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.dataclass.CommentSlops

class CommentServiceSlop(private val database: FirebaseDatabase) {

    fun addComment(comment: CommentSlops, onSuccess: () -> Unit, onFailure: (error: String) -> Unit) {
        val commentsRef = database.reference.child("commentslopes")
        val commentId = commentsRef.push().key
        if (commentId != null) {
            commentsRef.child(commentId).setValue(comment)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Unknown error")
                }
        } else {
            onFailure("Failed to generate comment ID")
        }
    }

    fun getCommentsForSlop(slopId: String, onSuccess: (List<CommentSlops>) -> Unit, onFailure: (error: String) -> Unit) {
        database.reference.child("commentslopes").orderByChild("slopID").equalTo(slopId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments = dataSnapshot.children.mapNotNull { it.getValue(CommentSlops::class.java) }
                    onSuccess(comments)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onFailure(databaseError.message)
                }
            })
    }
}
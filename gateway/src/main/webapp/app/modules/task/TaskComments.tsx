import React, { useEffect, useState } from 'react';
import { getTaskComments, addCommentToTask } from '../../shared/util/comment-api';
import { Comment } from '../../shared/model/comment.model';

interface Props {
  taskId: number;
}

const TaskComments: React.FC<Props> = ({ taskId }) => {
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);
  const [newComment, setNewComment] = useState('');
  const [saving, setSaving] = useState(false);

  const loadComments = () => {
    setLoading(true);
    getTaskComments(taskId).then(data => {
      setComments(data);
      setLoading(false);
    });
  };

  useEffect(() => {
    loadComments();
  }, [taskId]);

  const handleAddComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    setSaving(true);
    await addCommentToTask(taskId, newComment);
    setNewComment('');
    setSaving(false);
    loadComments();
  };

  if (loading) return <div>Cargando comentarios...</div>;

  return (
    <div>
      <h5>Comentarios</h5>
      <ul className="list-group mb-3">
        {comments.map(c => (
          <li key={c.id} className="list-group-item">
            <b>{c.author?.login || 'Usuario'}:</b> {c.content}
            <div className="text-muted" style={{ fontSize: '0.8em' }}>
              {c.createTime}
            </div>
          </li>
        ))}
        {comments.length === 0 && <li className="list-group-item">Sin comentarios</li>}
      </ul>
      <form onSubmit={handleAddComment} className="d-flex">
        <input
          className="form-control me-2"
          value={newComment}
          onChange={e => setNewComment(e.target.value)}
          placeholder="Escribe un comentario..."
          disabled={saving}
        />
        <button className="btn btn-primary" type="submit" disabled={saving || !newComment.trim()}>
          {saving ? 'Enviando...' : 'Comentar'}
        </button>
      </form>
    </div>
  );
};

export default TaskComments;

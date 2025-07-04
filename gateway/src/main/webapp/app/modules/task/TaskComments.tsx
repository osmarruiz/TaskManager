import React, { useEffect, useState, useCallback } from 'react';
import { getTaskComments, addCommentToTask } from '../../shared/util/comment-api';
import { Comment } from '../../shared/model/comment.model';
import { useLoading } from '../../shared/hooks/useLoading';
import { useApiError } from '../../shared/hooks/useApiError';

interface Props {
  taskId: number;
}

const TaskComments: React.FC<Props> = ({ taskId }) => {
  const { loading, error, startLoading, stopLoading, setLoadingError } = useLoading(true);
  const { handleError } = useApiError();
  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState('');
  const [saving, setSaving] = useState(false);

  const loadComments = useCallback(() => {
    startLoading();
    getTaskComments(taskId)
      .then(data => {
        setComments(data);
        stopLoading();
      })
      .catch(err => {
        handleError(err, 'Error al cargar comentarios');
        setComments([]);
        setLoadingError('No se pudieron cargar los comentarios');
      });
  }, [taskId, startLoading, stopLoading, setLoadingError, handleError]);

  useEffect(() => {
    loadComments();
  }, [loadComments]);

  const handleAddComment = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!newComment.trim()) return;
      setSaving(true);
      try {
        await addCommentToTask(taskId, newComment);
        setNewComment('');
        loadComments();
      } catch (err) {
        handleError(err, 'Error al agregar comentario');
      } finally {
        setSaving(false);
      }
    },
    [taskId, newComment, loadComments, handleError],
  );

  if (loading)
    return (
      <div className="text-center py-3">
        <div className="spinner-border spinner-border-sm" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
        <span className="ms-2">Cargando comentarios...</span>
      </div>
    );

  if (error)
    return (
      <div className="alert alert-danger">
        <i className="fas fa-exclamation-triangle me-2"></i>
        {error}
      </div>
    );

  return (
    <div>
      <h5>Comentarios</h5>
      <ul className="list-group mb-3">
        {comments.map(c => (
          <li key={c.id} className="list-group-item">
            <b>{c.author?.id ? ` ${c.author.id}` : 'Usuario'}:</b> {c.content}
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
          aria-label="Nuevo comentario"
        />
        <button
          className="btn btn-primary"
          type="submit"
          disabled={saving || !newComment.trim()}
          aria-label={saving ? 'Enviando comentario...' : 'Agregar comentario'}
        >
          {saving ? (
            <>
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Enviando...
            </>
          ) : (
            <>
              <i className="fas fa-paper-plane me-2" aria-hidden="true"></i>
              Comentar
            </>
          )}
        </button>
      </form>
    </div>
  );
};

export default TaskComments;

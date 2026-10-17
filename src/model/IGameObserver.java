package model;

public interface IGameObserver {
    /** Se llama cada vez que el sujeto (GameModel) notifica un cambio. */
    void onModelChanged(ModelChange change);
}

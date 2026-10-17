package model.dto;

/**
 * Copia exacta de los records del servidor
 * (ver README del servidor, sección "Package: protocol").
 * TODO: copiar los componentes de cada record desde el servidor.
 */
public final class Payloads {

    public record AnimalState() {}
    public record CupState() {}
    public record FlagState() {}
    public record EventState() {}
    public record ZoneInfo() {}
    public record StateUpdate() {}
    public record GameStarted() {}
    public record Summary() {}
}

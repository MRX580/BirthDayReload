package org.referix.birthDayReload.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jdbi.v3.core.Jdbi;
import org.referix.birthDayReload.playerdata.PlayerData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PlayerDataDAO {
    private final Jdbi jdbi;

    public PlayerDataDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public void savePlayerData(PlayerData data) {
        jdbi.useHandle(handle -> {
            String birthdayStr = (data.getBirthday() != null) ? data.getBirthday().toString() : null;
            handle.createUpdate("INSERT INTO player_data (player_uuid, birthday, is_wished, wished) " +
                            "VALUES (:uuid, :birthday, :isWished, :wished) " +
                            "ON CONFLICT(player_uuid) DO UPDATE SET " +
                            "birthday = :birthday, is_wished = :isWished, wished = :wished")
                    .bind("uuid", data.getPlayer().getUniqueId().toString())
                    .bind("birthday", birthdayStr)
                    .bind("isWished", data.isWished() ? 1 : 0)
                    .bind("wished", String.join(",", data.getWished()))
                    .execute();
        });
    }


    public Optional<PlayerData> loadPlayerData(Player player) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM player_data WHERE player_uuid = :uuid")
                .bind("uuid", player.getUniqueId().toString())
                .map((rs, ctx) -> {
                    String birthdayString = rs.getString("birthday");
                    LocalDate birthday = (birthdayString != null && !birthdayString.isEmpty())
                            ? LocalDate.parse(birthdayString)
                            : null;
                    return new PlayerData(
                            player,
                            birthday,
                            rs.getInt("is_wished") == 1,
                            List.of(rs.getString("wished").split(",")),
                            null
                    );
                })
                .findOne());
    }


    public void deletePlayerData(Player player) {
        jdbi.useHandle(handle -> handle.execute("DELETE FROM player_data WHERE player_uuid = ?", player.getUniqueId().toString()));
    }


    public List<PlayerData> loadAllPlayerData() {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM player_data")
                .map((rs, ctx) -> {
                    Player player = Bukkit.getPlayer(java.util.UUID.fromString(rs.getString("player_uuid")));
                    String birthdayString = rs.getString("birthday");
                    LocalDate birthday = (birthdayString != null && !birthdayString.isEmpty())
                            ? LocalDate.parse(birthdayString)
                            : null;
                    return new PlayerData(
                            player,
                            birthday,
                            rs.getInt("is_wished") == 1,
                            List.of(rs.getString("wished").split(",")),
                            null
                    );
                }).list());
    }

}

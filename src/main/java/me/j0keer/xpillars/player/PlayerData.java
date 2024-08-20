package me.j0keer.xpillars.player;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
public class PlayerData {
    private Double money = 0.0;
    private Integer kills = 0, deaths = 0, wins = 0, played = 0;
    private long playTime = 0L;

    public void setMoney(Double money) {
        this.money = money;
        setSaved(false);
    }

    public void addMoney(Double money) {
        this.money += money;
        setSaved(false);
    }

    public void removeMoney(Double money) {
        this.money -= money;
        setSaved(false);
    }

    public void setKills(Integer kills) {
        this.kills = kills;
        setSaved(false);
    }

    public void addKills(Integer kills) {
        this.kills += kills;
        setSaved(false);
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
        setSaved(false);
    }

    public void addDeaths(Integer deaths) {
        this.deaths += deaths;
        setSaved(false);
    }

    public void setWins(Integer wins) {
        this.wins = wins;
        setSaved(false);
    }

    public void addWins(Integer wins) {
        this.wins += wins;
        setSaved(false);
    }

    public void setPlayed(Integer played) {
        this.played = played;
        setSaved(false);
    }

    public void addPlayed(Integer played) {
        this.played += played;
        setSaved(false);
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
        setSaved(false);
    }

    public void addPlayTime(long playTime) {
        this.playTime += playTime;
        setSaved(false);
    }

    public void reset() {
        getSchema().forEach((key, data) -> data.set(data.defaultValue));
        setSaved(false);
    }

    @Setter
    private boolean saved = true;

    public HashMap<String, Data> getSchema() {
        HashMap<String, Data> schema = new HashMap<>();

        schema.put("money", new Data(Double.class, 0.0, money) {
            @Override
            public void set(Object value) {
                money = (Double) value;
            }
        });

        schema.put("kills", new Data(Integer.class, 0, kills) {
            @Override
            public void set(Object value) {
                kills = (Integer) value;
            }
        });

        schema.put("deaths", new Data(Integer.class, 0, deaths) {
            @Override
            public void set(Object value) {
                deaths = (Integer) value;
            }
        });

        schema.put("wins", new Data(Integer.class, 0, wins) {
            @Override
            public void set(Object value) {
                wins = (Integer) value;
            }
        });

        schema.put("played", new Data(Integer.class, 0, played) {
            @Override
            public void set(Object value) {
                played = (Integer) value;
            }
        });

        schema.put("playTime", new Data(Long.class, 0L, playTime) {
            @Override
            public void set(Object value) {
                playTime = (Long) value;
            }
        });

        return schema;
    }

    @Getter
    public abstract class Data {
        private final Object type;
        private final Object defaultValue;
        private final Object actualValue;

        public Data(Object type, Object defaultValue, Object actualValue) {
            this.type = type;
            this.defaultValue = defaultValue;
            this.actualValue = actualValue;
        }

        public abstract void set(Object value);
    }
}

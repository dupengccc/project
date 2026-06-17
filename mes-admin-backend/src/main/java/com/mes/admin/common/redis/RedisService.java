package com.mes.admin.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            return false;
        }
    }

    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return count == null ? 0 : count;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpire(String key) {
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl == null ? -1 : ttl;
        } catch (Exception e) {
            return -1;
        }
    }

    public long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result == null ? 0 : result;
        } catch (Exception e) {
            return 0;
        }
    }

    public long decrement(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().decrement(key, delta);
            return result == null ? 0 : result;
        } catch (Exception e) {
            return 0;
        }
    }

    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            return null;
        }
    }

    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public boolean hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public boolean lPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sAdd(String key, Object... values) {
        try {
            redisTemplate.opsForSet().add(key, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
}

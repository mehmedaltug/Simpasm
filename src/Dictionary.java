public class Dictionary<K,V> {
    private Object[] keys;
    private Object[] values;
    private int last_index = 0;
    private static final int BASE_SIZE = 50;
    Dictionary() {
        keys = new Object[BASE_SIZE];
        values = new Object[BASE_SIZE];
    }
    private void expand() {
        Object[] temp_keys =  new Object[keys.length * 2];
        Object[] temp_values = new Object[values.length * 2];
        System.arraycopy(keys, 0, temp_keys, 0, keys.length);
        System.arraycopy(values, 0, temp_values, 0, values.length);
        keys = temp_keys;
        values = temp_values;
    }
    public void add(K key, V value) {
        if(last_index == keys.length) expand();
        keys[last_index] = key;
        values[last_index] = value;
        last_index++;
    }
    public V get(K key) {
        for (int i = 0; i < last_index; i++) {
            if (keys[i].equals(key)) {
                return (V) values[i];
            }
        }
        return null;
    }
}

package charCount;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CharMapper<KEY> extends Mapper<KEY, Text, Text, LongWritable> {
	private static final int SIZE = 8;
	private static final LongWritable ONE = new LongWritable(1L);
	private StringBuilder builder = new StringBuilder(SIZE);
	private Text txt = new Text();
	private char[] cycle = new char[SIZE];

	public void map(KEY key, Text val, Context context) {
		String line = val.toString();

		// ~ Check if this line is worth spending effort on
		if (line.length() >= SIZE) {
			int i = 0;
			int j;

			// ~ Fill the buffer
			int useless = 0;
			for (j = 0; (j < SIZE - 1 + useless) && (j < line.length()); j++) {
				char currChar = Character.toLowerCase(line.charAt(j));

				// Only a-z
				if (isValid(currChar)) {
					this.cycle[i] = currChar;
					i++;
				} else {
					useless++;
				}
			}

			// ~ Start looping through the rest of the string
			for (; j < line.length(); j++) {
				char currChar = Character.toLowerCase(line.charAt(j));
				if (isValid(currChar)) {
					i = i % SIZE;
					this.cycle[i] = currChar;

					// Build the output from the cycle
					builder.delete(0, SIZE);
					builder.append(this.cycle, i, (this.cycle.length - i));
					builder.append(this.cycle, 0, i);

					i++;

					try {
						txt.set(builder.toString());
						context.write(txt, ONE);
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private boolean isValid(char c) {
		return ('a' <= c && c <= 'z') || c == ' ';
	}
}

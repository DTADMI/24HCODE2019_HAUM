package spectrumanalyzer;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.eclipse.paho.client.mqttv3.MqttException;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpListener {

    private TargetDataLine microphone;
    private boolean stopped;

    private static final double WAVEFORM_HEIGHT_COEFFICIENT = 1.3; // This fits the waveform to the swing node height

    private AudioFormat getFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 8;
        int channels = 1; //mono
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }


    public void ReadLine(boolean running) throws LineUnavailableException {
        final AudioFormat format = getFormat(); //Fill AudioFormat with the wanted settings
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        byte[] buffer = new byte[4096];

        //new Thread(() -> {
            OutputStream out = new ByteArrayOutputStream();
            running = true;

            try {
                while (running) {
                    int count = line.read(buffer, 0, buffer.length);
                    if (count > 0) {
                        out.write(buffer, 0, count);
                    } else {
                        break;
                    }
                }
                out.close();
            } catch (IOException e) {
                System.err.println("I/O problems: " + e);
                System.exit(-1);
            }
        //}).start();
    }

    private byte[] record() throws LineUnavailableException {
        AudioFormat format = getFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        // Checks if system supports the data line
        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line not supported");
            System.exit(0);
        }

        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        System.out.println("Listening, tap enter to stop ...");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int numBytesRead;
        byte[] data = new byte[microphone.getBufferSize() / 5];

        // Begin audio capture.
        microphone.start();

        // Here, stopped is a global boolean set by another thread.
        while (!stopped) {
            // Read the next chunk of data from the TargetDataLine.
            numBytesRead = microphone.read(data, 0, data.length);
            // Save this chunk of data.
            byteArrayOutputStream.write(data, 0, numBytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public void recordAndListen() throws LineUnavailableException {
        stopped = false;

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                stopped = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        byte[] byteArray = record();

        System.out.println(Arrays.toString(byteArray));

        byte[] copyByteArray = Arrays.copyOf(byteArray, byteArray.length);
        Arrays.sort(copyByteArray);

        int len =  copyByteArray.length;
        double per10 =  Math.floor(copyByteArray.length*.1) - 1;
        System.out.println("per10 :" + copyByteArray[(int)per10]);

        double per20 =  Math.floor(copyByteArray.length*.2) - 1;
        System.out.println("per20 :" + copyByteArray[(int)per20]);

        double per30 =  Math.floor(copyByteArray.length*.3) - 1;
        System.out.println("per30 :" + copyByteArray[(int)per30]);

        double per40 =  Math.floor(copyByteArray.length*.4) - 1;
        System.out.println("per40 :" + copyByteArray[(int)per40]);

        double per50 =  Math.floor(copyByteArray.length*.5) - 1;
        System.out.println("per50 :" + copyByteArray[(int)per50]);

        double per60 =  Math.floor(copyByteArray.length*.6) - 1;
        System.out.println("per60 :" + byteArray[(int)per60]);

        double per70 =  Math.floor(copyByteArray.length*.7) - 1;
        System.out.println("per70 :" + copyByteArray[(int)per70]);

        double per80 =  Math.floor(copyByteArray.length*.8) - 1;
        System.out.println("per80 :" + copyByteArray[(int)per80]);

        double per90 =  Math.floor(copyByteArray.length*.9) - 1;
        System.out.println("per90 :" + copyByteArray[(int)per90]);

        /*int[] intArray = toIntArray(byteArray);

        Percentile percentile = new Percentile(10);

        percentile.setData(intToDoubleArray(intArray));

        for(int i=10; i<=90; i=i+10){
            System.out.println("percentile " + i + " : " + percentile.evaluate(Double.valueOf(Integer.toString(i))));
        }*/
    }

    static void writePercentiles()
    {
        List<Integer> latencies = Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90);

        System.out.println(percentile(latencies,25));
        System.out.println(percentile(latencies, 50));
        System.out.println(percentile(latencies, 75));
        System.out.println(percentile(latencies, 100));
    }

    public static Integer percentile(List<Integer> latencies, double Percentile)
    {
        latencies.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1<o2 ? 1 : 0;
            }
        });
        int index = (int)Math.ceil(((double)Percentile / (double)100) * (double)latencies.size());
        return latencies.get(index-1);
    }

    public static double[] intToDoubleArray(int[] source) {
        double[] dest = new double[source.length];
        for(int i=0; i<source.length; i++) {
            dest[i] = source[i];
        }
        return dest;
    }

    public static double[] toDoubleArray(byte[] byteArray){
        int times = Double.SIZE / Byte.SIZE;
        double[] doubles = new double[byteArray.length / times];
        for(int i=0;i<doubles.length;i++){
            doubles[i] = ByteBuffer.wrap(byteArray, i*times, times).getDouble();
        }
        return doubles;
    }

    public static int[] toIntArray(byte[] byteArray){
        int times = Integer.SIZE / Byte.SIZE;
        int[] ints = new int[byteArray.length / times];
        for(int i=0;i<ints.length;i++){
            ints[i] = ByteBuffer.wrap(byteArray, i*times, times).getInt();
        }
        return ints;
    }

    /*public void fft(byte[] audio){
        //byte audio[] = out.toByteArray();

        final int totalSize = audio.length;

        int amountPossible = totalSize/Harvester.CHUNK_SIZE;

//When turning into frequency domain we'll need complex numbers:
        Complex[][] results = new Complex[amountPossible][];

//For all the chunks:
        for(int times = 0;times < amountPossible; times++) {
            Complex[] complex = new Complex[Harvester.CHUNK_SIZE];
            for(int i = 0;i < Harvester.CHUNK_SIZE;i++) {
                //Put the time domain data into a complex number with imaginary part as 0:
                complex[i] = new Complex(audio[(times*Harvester.CHUNK_SIZE)+i], 0);
            }
            //Perform FFT analysis on the chunk:
            results[times] = FFT.fft(complex);
        }
    }*/

    /**
     * Get Wav Amplitudes
     *
     * @param file
     * @return
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    private int[] getWavAmplitudes(File file) throws UnsupportedAudioFileException , IOException {
        System.out.println("Calculting WAV amplitudes");

        //Get Audio input stream
        try (AudioInputStream input = AudioSystem.getAudioInputStream(file)) {
            AudioFormat baseFormat = input.getFormat();

            //Encoding
            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_UNSIGNED;
            float sampleRate = baseFormat.getSampleRate();
            int numChannels = baseFormat.getChannels();

            AudioFormat decodedFormat = new AudioFormat(encoding, sampleRate, 16, numChannels, numChannels * 2, sampleRate, false);
            int available = input.available();

            //Get the PCM Decoded Audio Input Stream
            try (AudioInputStream pcmDecodedInput = AudioSystem.getAudioInputStream(decodedFormat, input)) {
                final int BUFFER_SIZE = 4096; //this is actually bytes

                //Create a buffer
                byte[] buffer = new byte[BUFFER_SIZE];

                //Now get the average to a smaller array
                int maximumArrayLength = 100000;
                int[] finalAmplitudes = new int[maximumArrayLength];
                int samplesPerPixel = available / maximumArrayLength;

                //Variables to calculate finalAmplitudes array
                int currentSampleCounter = 0;
                int arrayCellPosition = 0;
                float currentCellValue = 0.0f;

                //Variables for the loop
                int arrayCellValue = 0;

                //Read all the available data on chunks
                while (pcmDecodedInput.read(buffer, 0, BUFFER_SIZE) > 0)
                    for (int i = 0; i < buffer.length - 1; i += 2) {

                        //Calculate the value
                        arrayCellValue = (int) ( ( ( ( ( buffer[i + 1] << 8 ) | buffer[i] & 0xff ) << 16 ) / 32767 ) * WAVEFORM_HEIGHT_COEFFICIENT );

                        //Tricker
                        if (currentSampleCounter != samplesPerPixel) {
                            ++currentSampleCounter;
                            currentCellValue += Math.abs(arrayCellValue);
                        } else {
                            //Avoid ArrayIndexOutOfBoundsException
                            if (arrayCellPosition != maximumArrayLength)
                                finalAmplitudes[arrayCellPosition] = finalAmplitudes[arrayCellPosition + 1] = (int) currentCellValue / samplesPerPixel;

                            //Fix the variables
                            currentSampleCounter = 0;
                            currentCellValue = 0;
                            arrayCellPosition += 2;
                        }
                    }

                return finalAmplitudes;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }

        //You don't want this to reach here...
        return new int[1];
    }
   /* private int[] getWavAmplitudes() throws LineUnavailableException {
        System.out.println("Calculting WAV amplitudes");


        AudioFormat format = getFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        // Checks if system supports the data line
        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line not supported");
            System.exit(0);
        }

        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        System.out.println("Listening, tap enter to stop ...");

        //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int numBytesRead;
        byte[] data = new byte[microphone.getBufferSize() / 5];

        // Begin audio capture.
        microphone.start();

        final int BUFFER_SIZE = 4096; //this is actually bytes

        int available = microphone.available();
        //Create a buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        //Now get the average to a smaller array
        int maximumArrayLength = 100000;
        int[] finalAmplitudes = new int[maximumArrayLength];
        int samplesPerPixel = available / maximumArrayLength;

        //Variables to calculate finalAmplitudes array
        int currentSampleCounter = 0;
        int arrayCellPosition = 0;
        float currentCellValue = 0.0f;

        //Variables for the loop
        int arrayCellValue = 0;

        // Here, stopped is a global boolean set by another thread.
        while (!stopped) {
            // Read the next chunk of data from the TargetDataLine.
            numBytesRead = microphone.read(data, 0, data.length);
            // Save this chunk of data.
            //byteArrayOutputStream.write(data, 0, numBytesRead);

            for (int i = 0; i < data.length - 1; i += 2) {

                //Calculate the value
                arrayCellValue = (int) ( ( ( ( ( data[i + 1] << 8 ) | data[i] & 0xff ) << 16 ) / 32767 ) * WAVEFORM_HEIGHT_COEFFICIENT );

                //Tricker
                if (currentSampleCounter != samplesPerPixel) {
                    ++currentSampleCounter;
                    currentCellValue += Math.abs(arrayCellValue);
                } else {
                    //Avoid ArrayIndexOutOfBoundsException
                    if (arrayCellPosition != maximumArrayLength)
                        finalAmplitudes[arrayCellPosition] = finalAmplitudes[arrayCellPosition + 1] = (int) currentCellValue / samplesPerPixel;

                    //Fix the variables
                    currentSampleCounter = 0;
                    currentCellValue = 0;
                    arrayCellPosition += 2;
                }
            }
        }

        return finalAmplitudes;

    }*/

    /**
     * Process the amplitudes
     *
     * @param sourcePcmData
     * @return An array with amplitudes
     */
    private float[] processAmplitudes(int[] sourcePcmData, int width) {
        System.out.println("Processing WAV amplitudes");

        //The width of the resulting waveform panel
        //int width = waveVisualization.width;
        float[] waveData = new float[width];
        int samplesPerPixel = sourcePcmData.length / width;

        //Calculate
        float nValue;
        for (int w = 0; w < width; w++) {

            //For performance keep it here
            int c = w * samplesPerPixel;
            nValue = 0.0f;

            //Keep going
            for (int s = 0; s < samplesPerPixel; s++) {
                nValue += ( Math.abs(sourcePcmData[c + s]) / 65536.0f );
            }

            //Set WaveData
            waveData[w] = nValue / samplesPerPixel;
        }

        System.out.println("Finished Processing amplitudes");
        return waveData;
    }

    public static void main(String[] args) {
        try {
            UpListener upListener = new UpListener();
            upListener.recordAndListen();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkComponents;

import MiddleWare.CompressionAlgorithm;

/**
 *
 * @author Timothy
 */
public class Compressor extends CompressionAlgorithm{

    @Override
    public String compress(String dataStream) {
        dataStream = dataStream.replace(":MoveTowardCoordinatesMessage;", ":M;");
        dataStream = dataStream.replace("begin:", "@:");
        dataStream = dataStream.replace("end:", "!:");
        dataStream = dataStream.replace("@:stream;", "begin:stream;");
        dataStream = dataStream.replace("!:stream;", "end:stream;");
        return dataStream;
    }

    @Override
    public String decompress(String dataStream) {
        dataStream = dataStream.replace("@:", "begin:");
        dataStream = dataStream.replace("!:", "end:");
        dataStream = dataStream.replace(":P;", ":PelletPositionMessage;");
        dataStream = dataStream.replace(":B;", ":BlobStateMessage;");
        dataStream = dataStream.replace(":H;", ":HighScoreMessage;");
        dataStream = dataStream.replace(":C;", ":CurrentScoreMessage;");
        return dataStream;
    }
    
}

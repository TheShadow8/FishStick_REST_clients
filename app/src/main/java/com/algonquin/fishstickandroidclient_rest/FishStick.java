/* File: FishStick.java
 * Author: Chao Gu, Qiuju Zhu, Weikai Li, Di Yuan, Binh Minh Do
 * Date: April 2018
 * Transfer object for FishStick data
 *
 */

package com.algonquin.fishstickandroidclient_rest;


public class FishStick {

    public final String id;
    public final String recordNumber;
    public final String omega;
    public final String lambda;
    public final String uuid;

    public FishStick(String id, String recordNumber, String omega, String lambda, String uuid) {
     this.id = id;
     this.recordNumber = recordNumber;
     this.omega = omega;
     this.lambda = lambda;
     this.uuid = uuid;

    }
}

package com.knowgate.debug;

/**
 * This file is licensed under the Apache License version 2.0.
 * You may not use this file except in compliance with the license.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.
 */

/**
 * <p>Get elapsed time between to checkpoints</p>
 */
 
public final class Chronometer {
  private long tmStart;
  private long tmStop;

  /**
   * Default constructor
   */  
  public Chronometer() {
    tmStart = System.nanoTime();
    tmStop = -1l;
  }
  
  /**
   * Start chronometer
   */
  
  public void start() {
    tmStart = System.nanoTime();
  }
  
  /**
   * Stop chronometer
   * @return milliseconds elapsed since chronometer was started
   */
  public long stop() {
    tmStop = System.nanoTime();
    return elapsed();
  }

  /**
   * Get milliseconds elapsed between start and stop calls.
   * If stop() has not been called then return milliseconds between start and now.
   * If stop() has not been called then return milliseconds between when the Chronometer instance was created and now or stop time.
   * @return long milliseconds elapsed since chronometer was started
   */
  public long elapsed() {
	return ((tmStop==-1l ? System.nanoTime() : tmStop) - tmStart) / 1000l;
  }
  
} // Chronometer

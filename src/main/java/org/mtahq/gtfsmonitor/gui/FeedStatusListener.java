/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.gtfsmonitor.gui;

import java.util.List;

/**
 *
 * @author mnilsen
 */
public interface FeedStatusListener {
    void updateListData(List<FeedStatus> list);
}

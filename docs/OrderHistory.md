**Events**

- Order Created
    
        data: JSON string of Order data
        diff: null
        order: order entity
        oActivity: null
        event: 'Order Created'
        
- Order Updated

        data: JSON string of new Order data
        diff: diff
        order: order entity
        oActivity: null
        event: 'Order Updated'

- Activity Updated

        data: JSON string of new Activity data
        diff: diff
        order: order entity
        oActivity: oActivity entity
        event: 'Activity Updated'
        
- Timeline Overridden
    
        data: JSON string of new Activity data
        diff: diff
        order: order entity
        oActivity: oActivity entity
        event: 'Timeline Overridden'



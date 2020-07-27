(ns navi.core
  (:require [clojure.string :as s]))

(defn- optimized-scroll
  [type name]
  (let [running (atom nil)
        func (fn []
               (when-not @running
                 (reset! running true)
                 (js/requestAnimationFrame
                  (fn [_]
                    (.dispatchEvent
                     js/window
                     (js/CustomEvent. name))
                    (reset! running false)))))]
    (js/window.addEventListener type func)))

(defonce optimize-scroll-listener
  (optimized-scroll "scroll" "optimizedScroll"))

(defn register-navigation
  "Initializes the navigation logic and returns the :navi/start and :navi/stop
  handlers to respectively add and remove the scroll listener from the
  window. It takes the id or class of the menu container HTML element."
  [target]
  (let [state (atom nil)
        logic
        (fn []
          (let [nav-node (js/document.querySelector target)
                ;; to get nav offset from top plus nav height. With this, the
                ;; initial position of the content will always be exactly below the
                ;; nav, making it ideal for subnavs as well.
                nav-offset (fn [] (+ (.-offsetHeight nav-node)
                                     (.-top (.getBoundingClientRect nav-node))))
                ;; to get all <a> tags from user specified class or id
                link-elements (array-seq (.getElementsByTagName nav-node "a"))
                ;; to add ids from href attributes
                section-attrs (mapv
                               (fn [link-element]
                                 (let [href (.getAttribute link-element "href")]
                                   {:link link-element
                                    :href href
                                    :id (last (s/split href #"#"))}))
                               link-elements)
                ;; to add section nodes from ids. Note that we can grab the
                ;; relevant sections based on the <a> tag hrefs
                section-attrs-elements
                (mapv (fn [attrs]
                        (merge
                         attrs
                         {:node
                          (js/document.getElementById
                           (:id attrs))})) section-attrs)
                ;; callback to highlight the nav elements based on sections
                ;; position
                active-class-listener
                (fn []
                  (let [visited (filter
                                 (fn [{:keys [node]}]
                                   (let [;; to compute section distance from nav.
                                         ;; Negative value means content has been/is being viewed.
                                         ;; Zero means content is right below nav.
                                         ;; Positive means content hasn't reached the top yet,
                                         ;; so we discard it.
                                         section-offset
                                         (int (- (.-top (.getBoundingClientRect node))
                                                 (nav-offset)))]
                                     (or (zero? section-offset)
                                         (neg? section-offset))))
                                 section-attrs-elements)
                        ;; section currently being viewed. Calling last on the
                        ;; vec is enough as we can rely on the ordering, which
                        ;; means that non-last items have already been viewed.
                        active (last visited)]
                    ;; to remove active class from all <a> tags
                    (doseq [{:keys [^js link]} section-attrs-elements]
                      (.remove (.-classList link) "active"))
                    ;; to add active class to active <a> tag
                    (when-let [link (:link active)]
                      (.add (.-classList link) "active"))))]

            ;; ONLY SIDE EFFECTS
            ;; to add on click listener to link items
            (doseq [{:keys [^js link ^js node]} section-attrs-elements]
              (.addEventListener
               link "click"
               (fn [e]
                 (.preventDefault e)
                 (js/window.scrollTo #js {:top (- (.-offsetTop node)
                                                  (nav-offset))
                                          :behavior "smooth"})))
              section-attrs-elements)

            ;; to run callback on load
            (active-class-listener)
            ;; to return listener callback
            active-class-listener))]

    {:navi/start #(js/window.addEventListener "optimizedScroll" (reset! state (logic)))
     :navi/stop #(js/window.removeEventListener "optimizedScroll" @state)}))

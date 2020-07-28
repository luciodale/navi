# Navi

[![Clojars Project](https://img.shields.io/clojars/v/navi.svg)](https://clojars.org/navi)

*Navi* is a cljs library that provides a way to easily perform on scroll switching tabs during navigation. It doesn't bring it any dependencies, as it solely uses js interop. The current API consists of a single function, being `register-navigation`. The following GIF shows what you can achieve with *Navi* by simply calling the main function with any navbar id or class.

<img src="https://s7.gifyu.com/images/ezgif.com-video-to-gif9c779c192fe29bbc.gif" width="80%" />

## GIF Example Code

First, require navi and reagent:

```clojure
(ns your.ns
  (:require
   [reagent.core :as r]
   [navi.core :as navi]))
```

Then, write up the `body` function, which includes both the top menu and a submenu component, being `products`.

```clojure
(defn products []
  (let [{:navi/keys [start stop]}
        (navi/register-navigation "#subnav")]
    (r/create-class
     {:component-did-mount
      #(start)
      :component-will-unmount
      #(stop)
      :reagent-render
      (fn []
        [:div
         [:nav#subnav
          [:a.link {:href "#security"}
           "Security"]
          [:a.link {:href "#cloud"}
           "Cloud"]
          [:a.link {:href "#ecommerce"}
           "eCommerce"]
          [:a.link {:href "#apps"}
           "Native Apps"]]
         [:div#security.section
          "Security"]
         [:div#cloud.section
          "Cloud"]
         [:div#ecommerce.section
          "eCommerce"]
         [:div#apps.section
          "Native Apps"]])})))

(defn body []
  (let [{:navi/keys [start stop]}
        (navi/register-navigation "#nav")]
    (r/create-class
     {:component-did-mount
      #(start)
      :component-will-unmount
      #(stop)
      :reagent-render
      (fn []
        [:div
         [:nav#nav
          [:a.link {:href "#services"}
           "Services"]
          [:a.link {:href "#products"}
           "Products"]
          [:a.link {:href "#about"}
           "About"]
          [:a.link {:href "#contacts"}
           "Contacts"]]
         [:div#services.section
          "Services"]
         [:div#products.section
          [products]]
         [:div#about.section
          "About"]
         [:div#contacts.section
          "Contacts"]
         [:div
          {:style {:padding "1em"
                   :min-height "400px"}}
          "Footer"]])})))
```

The CSS for the components can be found here:

```css
#subnav {
    display: flex;
    width: 100%;
    position: sticky;
    top: 60px;
    font-size: .8em;
    background: black;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

#nav {
    z-index: 2;
    display: flex;
    align-items: flex-end;
    width: 100%;
    position: sticky;
    top: 0;
    height: 60px;
    background: black;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.link {
    padding: 1em;
    font-weight: bold;
    margin-right: 1em;
    color: lightgrey;
    text-decoration: none;
}

.link.active {
    color: white;
    background: black;
    border-bottom: 2px solid red;
    text-decoration: none;
}

.section {
    min-height: 600px;
    padding: 1em 0;
}

.section:nth-child(even) {
    background: darkgrey;
}

.section:nth-child(odd) {
    background: lightgrey;
}
```

## Next Features

The goal is to make this library more dynamic by specifying a set of user configurable options.
